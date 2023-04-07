package com.rn.service;

import com.rn.entity.EmailVerificationCredentials;
import com.rn.entity.EnumRole;
import com.rn.entity.RefreshToken;
import com.rn.entity.Role;
import com.rn.entity.User;
import com.rn.exception.CookieNotPresentException;
import com.rn.exception.InvalidPasswordOrUsernameException;
import com.rn.exception.InvalidTokenException;
import com.rn.exception.OccupiedValueException;
import com.rn.payload.SimpleResponseBody;
import com.rn.payload.SignInRequestBody;
import com.rn.payload.SignInResponseBody;
import com.rn.payload.SignUpRequestBody;
import com.rn.repository.EmailVerificationCredentialsRepository;
import com.rn.repository.RefreshTokenRepository;
import com.rn.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.Arrays;
import java.util.Date;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class JwtAuthService implements AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final EmailVerificationCredentialsRepository emailVerificationCredentialsRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthTokenService authTokenService;
    private final AuthEmailService authEmailService;
    private final String accessTokenCookieName;
    private final String refreshTokenCookieName;
    private final String accessTokenPath;
    private final String refreshTokenPath;
    private final String emailVerifiedLink;
    private final Integer emailVerificationRefreshTokenExpirationMs;
    private final Integer emailVerificationRefreshTokenActivationMs;
    private final Integer emailVerificationTokenExpirationMs;
    private final String emailVerificationRefreshTokenCookieName;
    private final String emailVerificationRefreshTokenPath;




    @Autowired
    private JwtAuthService(
        UserRepository userRepository,
        RefreshTokenRepository refreshTokenRepository,
        EmailVerificationCredentialsRepository emailVerificationCredentialsRepository,
        PasswordEncoder passwordEncoder,
        AuthTokenService authTokenService,
        AuthEmailService authEmailService,
        @Value("${RedNet.app.accessTokenCookieName}") String accessTokenCookieName,
        @Value("${RedNet.app.refreshTokenCookieName}") String refreshTokenCookieName,
        @Value("${RedNet.app.accessTokenPath}") String accessTokenPath,
        @Value("${RedNet.app.refreshTokenPath}") String refreshTokenPath,
        @Value("${RedNet.app.email.verifiedRedirectLink}") String emailVerifiedLink,
        @Value("${RedNet.app.email.verificationRefreshTokenExpirationMs}") Integer emailVerificationRefreshTokenExpirationMs,
        @Value("${RedNet.app.email.verificationRefreshTokenActivationMs}")Integer emailVerificationRefreshTokenActivationMs,
        @Value("${RedNet.app.email.verificationTokenExpirationMs}") Integer emailVerificationTokenExpirationMs,
        @Value("${RedNet.app.email.verificationRefreshTokenPath}") String emailVerificationRefreshTokenPath,
        @Value("${RedNet.app.email.verificationRefreshTokenCookieName}") String emailVerificationRefreshTokenCookieName
    ) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.emailVerificationCredentialsRepository = emailVerificationCredentialsRepository;
        this.passwordEncoder = passwordEncoder;
        this.authTokenService = authTokenService;
        this.authEmailService = authEmailService;
        this.accessTokenCookieName = accessTokenCookieName;
        this.refreshTokenCookieName = refreshTokenCookieName;
        this.accessTokenPath = accessTokenPath;
        this.refreshTokenPath = refreshTokenPath;
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

        setAuthentication(user);

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

        setAuthentication(user);

        String accessToken = authTokenService.generateAccessToken(user);
        String refreshToken;
        Optional<RefreshToken> optionalRefreshToken = refreshTokenRepository.findByUser_Id(user.getId());

        if(optionalRefreshToken.isPresent()) {
            if(authTokenService.isTokenValid(optionalRefreshToken.get().getToken())){
                refreshToken = optionalRefreshToken.get().getToken();
            } else {
                refreshToken = authTokenService.generateRefreshToken(user);
                refreshTokenRepository.updateToken(
                    optionalRefreshToken.get().getId(),
                    refreshToken,
                    authTokenService.extractExpiration(refreshToken)
                );
            }
        } else {
            refreshToken = authTokenService.generateRefreshToken(user);
            refreshTokenRepository.save(
                new RefreshToken(
                    refreshToken,
                    authTokenService.extractExpiration(refreshToken),
                    user
                )
            );
        }

        return ResponseEntity.ok()
            .header(
                HttpHeaders.SET_COOKIE,
                generateAccessCookie(accessToken).toString()
            )
            .header(
                HttpHeaders.SET_COOKIE,
                generateRefreshCookie(refreshToken).toString()
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
    public ResponseEntity<SimpleResponseBody> signOut(HttpServletRequest request) {
        SecurityContextHolder.getContext().setAuthentication(null);
        return ResponseEntity.ok()
            .header(
                HttpHeaders.SET_COOKIE,
                generateCleaningCookie(accessTokenCookieName,accessTokenPath).toString()
            )
            .header(
                HttpHeaders.SET_COOKIE,
                generateCleaningCookie(refreshTokenCookieName,refreshTokenPath).toString()
            )
            .body(new SimpleResponseBody("signout success"));
    }

    @Override
    public ResponseEntity<SimpleResponseBody> refreshToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();

        if (cookies == null) {
            throw new CookieNotPresentException();
        }

        String cookieRefreshToken = Arrays.stream(cookies)
            .filter(cookie -> cookie.getName().equals(refreshTokenCookieName))
            .findFirst().orElseThrow(CookieNotPresentException::new)
            .getValue();

        if (!authTokenService.isTokenValid(cookieRefreshToken)) {
            throw new InvalidTokenException(cookieRefreshToken);
        }

        Long userId = Long.valueOf(authTokenService.extractSubject(cookieRefreshToken));
        RefreshToken refreshTokenEntity = refreshTokenRepository.findEagerByUser_Id(userId)
            .orElseThrow(() -> new InvalidTokenException(cookieRefreshToken));
        String accessToken = authTokenService.generateAccessToken(refreshTokenEntity.getUser());
        String refreshToken = authTokenService.generateRefreshToken(refreshTokenEntity.getUser());

        refreshTokenRepository.updateToken(
            refreshTokenEntity.getId(),
            refreshToken,
            authTokenService.extractExpiration(refreshToken)
        );

        return ResponseEntity.ok()
            .header(
                HttpHeaders.SET_COOKIE,
                generateAccessCookie(accessToken).toString()
            )
            .header(
                HttpHeaders.SET_COOKIE,
                generateRefreshCookie(refreshToken).toString()
            )
            .body(new SimpleResponseBody("refresh success"));
    }

    @Override
    public ResponseEntity<SignInResponseBody> verifyEmail(String emailVerificationToken) {
        EmailVerificationCredentials emailVerificationCredentials = emailVerificationCredentialsRepository.findEagerByToken(emailVerificationToken)
            .orElseThrow(() -> new InvalidTokenException(emailVerificationToken));

        User user = emailVerificationCredentials.getUser();
        userRepository.enableUserById(user.getId());

        String accessToken = authTokenService.generateAccessToken(user);
        String refreshToken = authTokenService.generateRefreshToken(user);

        refreshTokenRepository.save(
            new RefreshToken(
                refreshToken,
                authTokenService.extractExpiration(refreshToken),
                user
            )
        );

        emailVerificationCredentialsRepository.deleteById(emailVerificationCredentials.getId());

        return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(emailVerifiedLink))
            .header(
                HttpHeaders.SET_COOKIE,
                generateCleaningCookie(emailVerificationRefreshTokenCookieName, emailVerificationRefreshTokenPath).toString()
            )
            .header(
                HttpHeaders.SET_COOKIE,
                generateAccessCookie(accessToken).toString()
            )
            .header(
                HttpHeaders.SET_COOKIE,
                generateRefreshCookie(refreshToken).toString()
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

    @Override
    public ResponseEntity<Boolean> verifyRequest(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();

        if (cookies == null) {
            throw new CookieNotPresentException();
        }

        String cookieToken = Arrays.stream(cookies)
            .filter(cookie -> cookie.getName().equals(accessTokenCookieName))
            .findFirst().orElseThrow(CookieNotPresentException::new)
            .getValue();

        if(!authTokenService.isTokenValid(cookieToken)) {
            throw new InvalidTokenException(cookieToken);
        }

        return ResponseEntity.ok(true);
    }




    private void setAuthentication(User user){
        SecurityContextHolder.getContext().setAuthentication(
            new UsernamePasswordAuthenticationToken(
                user.getId(),
                null,
                user.getRoles().stream()
                    .map(r -> new SimpleGrantedAuthority(r.getDesignation().name()))
                    .toList()
            )
        );
    }

    private ResponseCookie generateAccessCookie(String value) {
        return ResponseCookie.from(accessTokenCookieName, value)
            .path(accessTokenPath)
            .httpOnly(true)
            .maxAge(TimeUnit.MILLISECONDS.toSeconds(authTokenService.getAccessTokenExpirationMs()))
            .build();
    }

    private ResponseCookie generateRefreshCookie(String value) {
        return ResponseCookie.from(refreshTokenCookieName, value)
            .path(refreshTokenPath)
            .httpOnly(true)
            .maxAge(TimeUnit.MILLISECONDS.toSeconds(authTokenService.getRefreshTokenExpirationMs()))
            .build();
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
