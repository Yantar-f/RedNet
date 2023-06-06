package com.rn.service;

import com.rn.entity.EmailVerificationCredentials;
import com.rn.model.EnumRole;
import com.rn.entity.Role;
import com.rn.entity.User;
import com.rn.exception.CookieNotPresentException;
import com.rn.exception.InvalidPasswordOrUsernameException;
import com.rn.exception.InvalidTokenException;
import com.rn.exception.OccupiedValueException;
import com.rn.payload.UserAuthenticationRequestBody;
import com.rn.payload.UserAuthenticationResponseBody;
import com.rn.payload.UserCreationRequestBody;
import com.rn.repository.EmailVerificationCredentialsRepository;
import com.rn.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Date;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final EmailVerificationCredentialsRepository emailVerificationCredentialsRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final Integer emailVerificationRefreshTokenExpirationMs;
    private final Integer emailVerificationRefreshTokenActivationMs;
    private final Integer emailVerificationTokenExpirationMs;
    private final String emailVerificationRefreshTokenCookieName;
    private final String emailVerificationRefreshTokenPath;
    private static class InnerHttpHeaders{
        static final String AUTH_USER_ID = "Auth-User-Id";
        static final String AUTH_USER_ROLE = "Auth-User-Role";
    }




    @Autowired
    private UserServiceImpl(
        UserRepository userRepository,
        EmailVerificationCredentialsRepository emailVerificationCredentialsRepository,
        PasswordEncoder passwordEncoder,
        EmailService emailService,
        @Value("${RedNet.app.email.verificationRefreshTokenExpirationMs}") Integer emailVerificationRefreshTokenExpirationMs,
        @Value("${RedNet.app.email.verificationRefreshTokenActivationMs}")Integer emailVerificationRefreshTokenActivationMs,
        @Value("${RedNet.app.email.verificationTokenExpirationMs}") Integer emailVerificationTokenExpirationMs,
        @Value("${RedNet.app.email.verificationRefreshTokenPath}") String emailVerificationRefreshTokenPath,
        @Value("${RedNet.app.email.verificationRefreshTokenCookieName}") String emailVerificationRefreshTokenCookieName
    ) {
        this.userRepository = userRepository;
        this.emailVerificationCredentialsRepository = emailVerificationCredentialsRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.emailVerificationRefreshTokenExpirationMs = emailVerificationRefreshTokenExpirationMs;
        this.emailVerificationRefreshTokenActivationMs = emailVerificationRefreshTokenActivationMs;
        this.emailVerificationTokenExpirationMs = emailVerificationTokenExpirationMs;
        this.emailVerificationRefreshTokenPath = emailVerificationRefreshTokenPath;
        this.emailVerificationRefreshTokenCookieName = emailVerificationRefreshTokenCookieName;
    }




    @Override
    public ResponseEntity<Object> create(UserCreationRequestBody requestBody) {
        if (userRepository.existsByUsernameOrEmail(requestBody.getUsername(),requestBody.getEmail())) {
            throw new OccupiedValueException("Username or Email is occupied");
        }

        User user = new User(
            requestBody.getUsername(),
            requestBody.getEmail(),
            passwordEncoder.encode(requestBody.getPassword()),
            Set.of(new Role(EnumRole.ROLE_USER))
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

        emailService.sendEmail(user.getEmail(),emailVerificationCredentials.getToken());

        return ResponseEntity.ok().header(
            HttpHeaders.SET_COOKIE,
            generateEmailVerificationCookie(emailVerificationCredentials.getRefreshToken()).toString()
        ).build();
    }

    @Override
    public ResponseEntity<UserAuthenticationResponseBody> authenticate(UserAuthenticationRequestBody requestBody) {
        User user = userRepository.findEagerByUsernameAndEnabled(requestBody.getUsername(), true)
            .orElseThrow(InvalidPasswordOrUsernameException::new);

        if(!passwordEncoder.matches(requestBody.getPassword(),user.getPassword())) {
            throw new InvalidPasswordOrUsernameException();
        }

        ResponseEntity.BodyBuilder responseBuilder = ResponseEntity.ok().header(
                InnerHttpHeaders.AUTH_USER_ID,
                user.getId().toString()
        );

        user.getRoles().forEach(role -> {
            responseBuilder.header(
                InnerHttpHeaders.AUTH_USER_ROLE,
                role.getDesignation().name()
            );
        });

        return responseBuilder.body(
            new UserAuthenticationResponseBody(
                user.getId().toString(),
                user.getRoles().stream()
                    .map(role -> role.getDesignation().name())
                    .toList()
            )
        );
    }

    @Override
    public ResponseEntity<UserAuthenticationResponseBody> verifyEmail(String emailVerificationToken) {
        EmailVerificationCredentials emailVerificationCredentials = emailVerificationCredentialsRepository.findEagerByToken(emailVerificationToken)
            .orElseThrow(() -> new InvalidTokenException(emailVerificationToken));

        User user = emailVerificationCredentials.getUser();
        userRepository.enableUserById(user.getId());

        emailVerificationCredentialsRepository.deleteById(emailVerificationCredentials.getId());

        ResponseEntity.BodyBuilder responseBuilder = ResponseEntity.ok()
            .header(
                InnerHttpHeaders.AUTH_USER_ID,
                user.getId().toString()
            )
            .header(
                HttpHeaders.SET_COOKIE,
                generateCleaningCookie(emailVerificationRefreshTokenCookieName, emailVerificationRefreshTokenPath).toString()
            );

        user.getRoles().forEach(role -> {
            responseBuilder.header(
                InnerHttpHeaders.AUTH_USER_ROLE,
                role.getDesignation().name()
            );
        });

        return responseBuilder.body(
            new UserAuthenticationResponseBody(
                user.getId().toString(),
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

        emailService.sendEmail(emailVerificationCredentials.getUser().getEmail(),emailVerificationToken);
        return ResponseEntity.ok().header(
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
