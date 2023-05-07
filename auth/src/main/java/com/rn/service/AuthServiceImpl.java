package com.rn.service;

import com.rn.entity.EmailVerificationCredentials;
import com.rn.model.EnumRole;
import com.rn.entity.Role;
import com.rn.entity.User;
import com.rn.exception.CookieNotPresentException;
import com.rn.exception.InvalidPasswordOrUsernameException;
import com.rn.exception.InvalidTokenException;
import com.rn.exception.OccupiedValueException;
import com.rn.payload.SignInRequestBody;
import com.rn.payload.SignInResponseBody;
import com.rn.payload.SignUpRequestBody;
import com.rn.repository.EmailVerificationCredentialsRepository;
import com.rn.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.Arrays;
import java.util.Date;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final EmailVerificationCredentialsRepository emailVerificationCredentialsRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthEmailService authEmailService;
    private final String emailVerifiedLink;
    private final Integer emailVerificationRefreshTokenExpirationMs;
    private final Integer emailVerificationRefreshTokenActivationMs;
    private final Integer emailVerificationTokenExpirationMs;
    private final String emailVerificationRefreshTokenCookieName;
    private final String emailVerificationRefreshTokenPath;




    @Autowired
    private AuthServiceImpl(
        UserRepository userRepository,
        EmailVerificationCredentialsRepository emailVerificationCredentialsRepository,
        PasswordEncoder passwordEncoder,
        AuthEmailService authEmailService,
        @Value("${RedNet.app.email.verifiedRedirectLink}") String emailVerifiedLink,
        @Value("${RedNet.app.email.verificationRefreshTokenExpirationMs}") Integer emailVerificationRefreshTokenExpirationMs,
        @Value("${RedNet.app.email.verificationRefreshTokenActivationMs}")Integer emailVerificationRefreshTokenActivationMs,
        @Value("${RedNet.app.email.verificationTokenExpirationMs}") Integer emailVerificationTokenExpirationMs,
        @Value("${RedNet.app.email.verificationRefreshTokenPath}") String emailVerificationRefreshTokenPath,
        @Value("${RedNet.app.email.verificationRefreshTokenCookieName}") String emailVerificationRefreshTokenCookieName
    ) {
        this.userRepository = userRepository;
        this.emailVerificationCredentialsRepository = emailVerificationCredentialsRepository;
        this.passwordEncoder = passwordEncoder;
        this.authEmailService = authEmailService;
        this.emailVerifiedLink = emailVerifiedLink;
        this.emailVerificationRefreshTokenExpirationMs = emailVerificationRefreshTokenExpirationMs;
        this.emailVerificationRefreshTokenActivationMs = emailVerificationRefreshTokenActivationMs;
        this.emailVerificationTokenExpirationMs = emailVerificationTokenExpirationMs;
        this.emailVerificationRefreshTokenPath = emailVerificationRefreshTokenPath;
        this.emailVerificationRefreshTokenCookieName = emailVerificationRefreshTokenCookieName;
    }




    @Override
    public ResponseEntity<Object> signUp(SignUpRequestBody requestBody) {
        if (userRepository.existsByUsernameOrEmail(requestBody.getUsername(),requestBody.getEmail())) {
            throw new OccupiedValueException("Username or Email is occupied");
        }

        User user = new User(
            requestBody.getUsername(),
            requestBody.getEmail(),
            passwordEncoder.encode(requestBody.getPassword()),
            Set.of(new Role(EnumRole.USER))
        );

        userRepository.save(user);

        EmailVerificationCredentials emailVerificationCredentials = new EmailVerificationCredentials(
            UUID.randomUUID().toString(),
            UUID.randomUUID().toString(),
            new Date(System.currentTimeMillis() + emailVerificationTokenExpirationMs),
            new Date(System.currentTimeMillis() + emailVerificationRefreshTokenActivationMs),
            new Date(System.currentTimeMillis() + emailVerificationRefreshTokenExpirationMs),
            user
        );

        emailVerificationCredentialsRepository.save(emailVerificationCredentials);

        authEmailService.sendEmail(user.getEmail(),emailVerificationCredentials.getToken());

        return ResponseEntity.ok()
            .header(
                HttpHeaders.SET_COOKIE,
                generateEmailVerificationCookie(emailVerificationCredentials.getRefreshToken()).toString()
            ).build();
    }

    @Override
    public ResponseEntity<SignInResponseBody> signIn(SignInRequestBody requestBody) {
        User user = userRepository.findEagerByUsernameAndEnabled(requestBody.getUsername(), true)
            .orElseThrow(InvalidPasswordOrUsernameException::new);

        if(!passwordEncoder.matches(requestBody.getPassword(),user.getPassword())) {
            throw new InvalidPasswordOrUsernameException();
        }

        return ResponseEntity.ok()
            .body(
                new SignInResponseBody(
                    user.getUsername(),
                    user.getRoles().stream()
                        .map(role -> role.getDesignation().name())
                        .toList()
                )
            );
    }

    @Override
    public ResponseEntity<SignInResponseBody> verifyEmail(String emailVerificationToken) {
        EmailVerificationCredentials emailVerificationCredentials = emailVerificationCredentialsRepository.findEagerByToken(emailVerificationToken)
            .orElseThrow(() -> new InvalidTokenException(emailVerificationToken));

        User user = emailVerificationCredentials.getUser();
        userRepository.enableUserById(user.getId());

        emailVerificationCredentialsRepository.deleteById(emailVerificationCredentials.getId());

        return ResponseEntity.ok()
            .header(
                HttpHeaders.SET_COOKIE,
                generateCleaningCookie(emailVerificationRefreshTokenCookieName, emailVerificationRefreshTokenPath).toString()
            )
            .body(
                new SignInResponseBody(
                    user.getUsername(),
                    user.getRoles().stream()
                        .map(role -> role.getDesignation().name())
                        .toList()
                )
            );
    }

    @Override
    public ResponseEntity<Object> resendEmailVerification(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();

        if (cookies == null) {
            throw new CookieNotPresentException();
        }

        String cookieEmailVerificationRefreshToken = Arrays.stream(cookies)
            .filter(cookie -> cookie.getName().equals(emailVerificationRefreshTokenCookieName))
            .findFirst().orElseThrow(CookieNotPresentException::new)
            .getValue();
        EmailVerificationCredentials emailVerificationCredentials = emailVerificationCredentialsRepository.findWithUserByRefreshToken(cookieEmailVerificationRefreshToken)
            .orElseThrow(() -> new InvalidTokenException(cookieEmailVerificationRefreshToken));
        Date curDate = new Date();

        if(
            emailVerificationCredentials.getRefreshTokenActivation().after(curDate) ||
            emailVerificationCredentials.getRefreshTokenExpiration().before(curDate)
        ) {
            throw new InvalidTokenException(emailVerificationCredentials.getRefreshToken());
        }

        String emailVerificationRefreshToken = UUID.randomUUID().toString();
        String emailVerificationToken = UUID.randomUUID().toString();

        emailVerificationCredentialsRepository.updateCredentialsByRefreshToken(
            cookieEmailVerificationRefreshToken,
            emailVerificationToken,
            emailVerificationRefreshToken,
            new Date(System.currentTimeMillis() + emailVerificationTokenExpirationMs),
            new Date(System.currentTimeMillis() + emailVerificationRefreshTokenActivationMs),
            new Date(System.currentTimeMillis() + emailVerificationRefreshTokenExpirationMs)
        );

        authEmailService.sendEmail(emailVerificationCredentials.getUser().getEmail(),emailVerificationToken);
        return ResponseEntity.ok()
            .header(
                HttpHeaders.SET_COOKIE,
                generateEmailVerificationCookie(emailVerificationRefreshToken).toString()
            ).build();
    }




    private ResponseCookie generateEmailVerificationCookie(String value) {
        return ResponseCookie.from(emailVerificationRefreshTokenCookieName, value)
            .path(emailVerificationRefreshTokenPath)
            .httpOnly(true)
            .maxAge(TimeUnit.MILLISECONDS.toSeconds(emailVerificationRefreshTokenExpirationMs))
            .build();
    }

    private ResponseCookie generateCleaningCookie(String name,String path) {
        return ResponseCookie.from(name)
            .path(path)
            .maxAge(0)
            .build();
    }
}
