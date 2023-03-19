package com.rn.auth.service;

import com.rn.auth.entity.EnumRole;
import com.rn.auth.entity.RefreshToken;
import com.rn.auth.entity.Role;
import com.rn.auth.entity.User;
import com.rn.auth.payload.SimpleResponseBody;
import com.rn.auth.payload.SignInRequestBody;
import com.rn.auth.payload.SignInResponseBody;
import com.rn.auth.payload.SignUpRequestBody;
import com.rn.auth.repository.RefreshTokenRepository;
import com.rn.auth.repository.UserRepository;
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
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
public class JwtAuthService implements AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthTokenService authTokenService;
    private final AuthEmailService authEmailService;
    private final String accessTokenCookieName;
    private final String refreshTokenCookieName;
    private final String accessTokenPath;
    private final String refreshTokenPath;
    private final String emailVerifiedLink;




    @Autowired
    private JwtAuthService(
        UserRepository userRepository,
        RefreshTokenRepository refreshTokenRepository,
        PasswordEncoder passwordEncoder,
        AuthTokenService authTokenService,
        AuthEmailService authEmailService,
        @Value("${RedNet.app.accessTokenCookieName}") String accessTokenCookieName,
        @Value("${RedNet.app.refreshTokenCookieName}") String refreshTokenCookieName,
        @Value("${RedNet.app.accessTokenPath}") String accessTokenPath,
        @Value("${RedNet.app.refreshTokenPath}") String refreshTokenPath,
        @Value("${RedNet.app.emailVerifiedLink}") String emailVerifiedLink
    ) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.authTokenService = authTokenService;
        this.authEmailService = authEmailService;
        this.accessTokenCookieName = accessTokenCookieName;
        this.refreshTokenCookieName = refreshTokenCookieName;
        this.accessTokenPath = accessTokenPath;
        this.refreshTokenPath = refreshTokenPath;
        this.emailVerifiedLink = emailVerifiedLink;
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

        authEmailService.sendEmail(user.getEmail(), authTokenService.generateEmailVerificationToken(user));

        return ResponseEntity.ok().build();
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
    public ResponseEntity<SimpleResponseBody> refresh(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();

        if (cookies == null) {
            throw new CookieNotPresentException();
        }

        String cookieRefreshToken = Arrays.stream(cookies)
            .filter(cookie -> cookie.getName().equals(refreshTokenCookieName))
            .findFirst().orElseThrow(() -> new CookieNotPresentException(refreshTokenCookieName))
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
    public ResponseEntity<SignInResponseBody> verify(String token) {
        if(!authTokenService.isTokenValid(token)) {
            throw new InvalidTokenException(token);
        }

        Long userId = Long.valueOf(authTokenService.extractSubject(token));
        userRepository.enableUserById(userId);

        User user = userRepository.findEagerById(userId)
            .orElseThrow(InvalidPasswordOrUsernameException::new);
        String accessToken = authTokenService.generateAccessToken(user);
        String refreshToken = authTokenService.generateRefreshToken(user);

        refreshTokenRepository.save(
            new RefreshToken(
                refreshToken,
                authTokenService.extractExpiration(refreshToken),
                user
            )
        );

        return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(emailVerifiedLink))
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

    private ResponseCookie generateCleaningCookie(String name,String path) {
        return ResponseCookie.from(name)
            .path(path)
            .maxAge(0)
            .build();
    }
}
