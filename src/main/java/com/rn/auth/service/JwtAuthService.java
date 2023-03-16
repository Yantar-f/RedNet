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
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
public class JwtAuthService implements AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final String accessTokenCookieName;
    private final String refreshTokenCookieName;
    private final String accessTokenPath;
    private final String refreshTokenPath;




    @Autowired
    private JwtAuthService(
        UserRepository userRepository,
        RefreshTokenRepository refreshTokenRepository,
        PasswordEncoder passwordEncoder,
        TokenService tokenService,
        @Value("${RedNet.app.accessTokenCookieName}") String accessTokenCookieName,
        @Value("${RedNet.app.refreshTokenCookieName}") String refreshTokenCookieName,
        @Value("${RedNet.app.accessTokenPath}") String accessTokenPath,
        @Value("${RedNet.app.refreshTokenPath}") String refreshTokenPath
    ) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
        this.accessTokenCookieName = accessTokenCookieName;
        this.refreshTokenCookieName = refreshTokenCookieName;
        this.accessTokenPath = accessTokenPath;
        this.refreshTokenPath = refreshTokenPath;
    }




    @Override
    public ResponseEntity<SignInResponseBody> signUp(SignUpRequestBody requestBody) {
        if (userRepository.existsByUsernameOrEmail(requestBody.getUsername(),requestBody.getEmail())) {
            throw new OccupiedValueException("Username or Email is occupied");
        }

        User user = new User(
            requestBody.getUsername(),
            requestBody.getEmail(),
            passwordEncoder.encode(requestBody.getPassword())
        );
        Role role = new Role(EnumRole.USER);
        user.setRoles(Set.of(role));

        userRepository.save(user);

        setAuthentication(user);

        String accessToken = tokenService.generateAccessToken(user);
        String refreshToken = tokenService.generateRefreshToken(user);
        RefreshToken refreshTokenEntity = new RefreshToken(refreshToken,user);

        refreshTokenRepository.save(refreshTokenEntity);

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
                    requestBody.getUsername(),
                    List.of(role.getDesignation().name())
                )
            );
    }

    @Override
    public ResponseEntity<SignInResponseBody> signIn(SignInRequestBody requestBody) {
        User user = userRepository.findByUsername(requestBody.getUsername())
            .orElseThrow(InvalidPasswordOrUsernameException::new);

        if(!passwordEncoder.matches(requestBody.getPassword(),user.getPassword())){
            throw new InvalidPasswordOrUsernameException();
        }

        setAuthentication(user);

        String accessToken = tokenService.generateAccessToken(user);
        RefreshToken refreshTokenEntity = refreshTokenRepository.findByUser_Id(user.getId()).orElseGet(() -> {
                RefreshToken refreshTokenEntityTmp = new RefreshToken(
                    tokenService.generateRefreshToken(user),
                    user
                );
                refreshTokenRepository.save(refreshTokenEntityTmp);
                return refreshTokenEntityTmp;
            });

        if(!tokenService.isTokenValid(refreshTokenEntity.getToken())){
            refreshTokenEntity.setToken(tokenService.generateRefreshToken(user));
            refreshTokenRepository.updateToken(refreshTokenEntity.getId(),refreshTokenEntity.getToken());
        }

        String refreshToken = refreshTokenEntity.getToken();

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

        if (!tokenService.isTokenValid(cookieRefreshToken)) {
            throw new InvalidTokenException(cookieRefreshToken);
        }

        Long userId = Long.valueOf(tokenService.extractSubject(cookieRefreshToken));
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new InvalidTokenException(cookieRefreshToken));
        String accessToken = tokenService.generateAccessToken(user);
        String refreshToken = tokenService.generateRefreshToken(user);
        RefreshToken refreshTokenEntity = refreshTokenRepository.findByUser_Id(user.getId())
            .orElseThrow(() -> new InvalidTokenException(cookieRefreshToken));

        refreshTokenRepository.updateToken(refreshTokenEntity.getId(),refreshToken);

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
            .maxAge(TimeUnit.MILLISECONDS.toSeconds(tokenService.getAccessTokenExpirationMs()))
            .build();
    }

    private ResponseCookie generateRefreshCookie(String value) {
        return ResponseCookie.from(refreshTokenCookieName, value)
            .path(refreshTokenPath)
            .httpOnly(true)
            .maxAge(TimeUnit.MILLISECONDS.toSeconds(tokenService.getRefreshTokenExpirationMs()))
            .build();
    }

    private ResponseCookie generateCleaningCookie(String name,String path) {
        return ResponseCookie.from(name)
            .path(path)
            .maxAge(0)
            .build();
    }
}
