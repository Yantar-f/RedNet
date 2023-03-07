package com.rn.auth.service;

import com.rn.auth.model.UserDetailsImpl;
import com.rn.auth.entity.EnumRole;
import com.rn.auth.entity.RefreshToken;
import com.rn.auth.entity.Role;
import com.rn.auth.entity.User;
import com.rn.auth.payload.SimpleResponseBody;
import com.rn.auth.payload.SignInRequestBody;
import com.rn.auth.payload.SignInResponseBody;
import com.rn.auth.payload.SignUpRequestBody;
import com.rn.auth.repository.RefreshTokenRepository;
import com.rn.auth.repository.RoleRepository;
import com.rn.auth.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
public class JwtAuthService implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final AuthenticationManager authenticationManager;
    private final String accessTokenCookieName;
    private final String refreshTokenCookieName;
    private final String accessTokenPath;
    private final String refreshTokenPath;




    @Autowired
    private JwtAuthService(
        UserRepository userRepository,
        RoleRepository roleRepository,
        RefreshTokenRepository refreshTokenRepository,
        PasswordEncoder passwordEncoder,
        TokenService tokenService,
        AuthenticationManager authenticationManager,
        @Value("${RedNet.app.accessTokenCookieName}") String accessTokenCookieName,
        @Value("${RedNet.app.refreshTokenCookieName}") String refreshTokenCookieName,
        @Value("${RedNet.app.accessTokenPath}") String accessTokenPath,
        @Value("${RedNet.app.refreshTokenPath}") String refreshTokenPath
    ) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
        this.authenticationManager = authenticationManager;
        this.accessTokenCookieName = accessTokenCookieName;
        this.refreshTokenCookieName = refreshTokenCookieName;
        this.accessTokenPath = accessTokenPath;
        this.refreshTokenPath = refreshTokenPath;
    }




    @Override
    public ResponseEntity<SignInResponseBody> signUp(SignUpRequestBody requestBody) {

        //if username exists????///
        User user = new User(
            requestBody.getUsername(),
            requestBody.getEmail(),
            passwordEncoder.encode(requestBody.getPassword()));
        Role role = roleRepository
            .findByDesignation(EnumRole.USER)
            .orElseThrow();

        user.setRoles(Set.of(role));

        userRepository.save(user);

        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                requestBody.getUsername(),
                requestBody.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetails userDetails = new UserDetailsImpl(user);
        String accessToken = tokenService.generateAccessToken(userDetails);
        String refreshToken = tokenService.generateRefreshToken(userDetails);
        RefreshToken refreshTokenEntity = new RefreshToken(refreshToken);

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
            .body(new SignInResponseBody(List.of(role.getDesignation().name())));
    }

    @Override
    public ResponseEntity<SignInResponseBody> signIn(SignInRequestBody requestBody) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                requestBody.getUsername(),
                requestBody.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user = userRepository
            .findByUsername(requestBody.getUsername())
            .orElseThrow();
        List<String> roles = user
            .getRoles()
            .stream()
            .map(role -> role.getDesignation().name()).toList();
        UserDetails userDetails = new UserDetailsImpl(user);
        String accessToken = tokenService.generateAccessToken(userDetails);
        String refreshToken = tokenService.generateRefreshToken(userDetails);

        ////////////////////////////////////////////////
        ////////////////////////////////////////////////
        ////////////////////////////////////////////////

        return ResponseEntity.ok()
            .header(
                HttpHeaders.SET_COOKIE,
                generateAccessCookie(accessToken).toString()
            )
            .header(
                HttpHeaders.SET_COOKIE,
                generateRefreshCookie(refreshToken).toString()
            )
            .body(new SignInResponseBody(roles));
    }

    @Override
    public ResponseEntity<SimpleResponseBody> signOut(HttpServletRequest request) {
        ////////////////////////////////////////////////
        ////////////////////////////////////////////////
        ////////////////////////////////////////////////

        return ResponseEntity.ok()
            .header(
                HttpHeaders.SET_COOKIE,
                generateCleaningCookie(accessTokenCookieName).toString()
            )
            .header(
                HttpHeaders.SET_COOKIE,
                generateCleaningCookie(refreshTokenCookieName).toString()
            )
            .body(new SimpleResponseBody("signout success"));
    }

    @Override
    public ResponseEntity<SimpleResponseBody> refresh(HttpServletRequest request) {
        ////////////////////////////////////////////////
        String accessToken = "";
        String refreshToken = "";
        ////////////////////////////////////////////////
        ////////////////////////////////////////////////


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

    private ResponseCookie generateCleaningCookie(String name) {
        return ResponseCookie.from(name)
            .maxAge(0)
            .build();
    }
}
