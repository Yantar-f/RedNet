package com.rn.auth.service;

import com.rn.auth.model.UserDetailsImpl;
import com.rn.auth.model.entity.EnumRole;
import com.rn.auth.model.entity.Role;
import com.rn.auth.model.entity.User;
import com.rn.auth.model.payload.SimpleResponseBody;
import com.rn.auth.model.payload.SignInRequestBody;
import com.rn.auth.model.payload.SignInResponseBody;
import com.rn.auth.model.payload.SignUpRequestBody;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthTokenService authTokenService;
    private final AuthenticationManager authenticationManager;
    private final String accessTokenCookieName;
    private final String refreshTokenCookieName;
    private final String accessTokenPath;
    private final String refreshTokenPath;




    @Autowired
    private AuthServiceImpl(
        UserRepository userRepository,
        RoleRepository roleRepository,
        PasswordEncoder passwordEncoder,
        AuthTokenService authTokenService,
        AuthenticationManager authenticationManager,
        @Value("${RedNet.app.accessTokenCookieName}") String accessTokenCookieName,
        @Value("${RedNet.app.refreshTokenCookieName}") String refreshTokenCookieName,
        @Value("${RedNet.app.accessTokenPath}") String accessTokenPath,
        @Value("${RedNet.app.refreshTokenPath}") String refreshTokenPath
    ) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.authTokenService = authTokenService;
        this.authenticationManager = authenticationManager;
        this.accessTokenCookieName = accessTokenCookieName;
        this.refreshTokenCookieName = refreshTokenCookieName;
        this.accessTokenPath = accessTokenPath;
        this.refreshTokenPath = refreshTokenPath;
    }




    @Override
    public ResponseEntity<SignInResponseBody> signUp(SignUpRequestBody requestBody) {
        User user = new User();
        Role role = roleRepository
            .findByDesignation(EnumRole.USER)
            .orElseThrow();

        user.setUsername(requestBody.getUsername());
        user.setEmail(requestBody.getEmail());
        user.setPassword(passwordEncoder.encode(requestBody.getPassword()));
        user.setRoles(Set.of(role));

        userRepository.save(user);

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        requestBody.getUsername(),
                        requestBody.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = authTokenService.generateAccessToken(new UserDetailsImpl(user));

        return ResponseEntity.ok(new SignInResponseBody(List.of(role.getDesignation().name())));
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
        String token = authTokenService.generateAccessToken(new UserDetailsImpl(user));
        List<String> roles = user
            .getRoles()
            .stream()
            .map(role -> role.getDesignation().name()).toList();

        return ResponseEntity.ok(new SignInResponseBody(roles));
    }

    @Override
    public ResponseEntity<SimpleResponseBody> signOut(HttpServletRequest request) {
        ////////////////////////////////////////////////
        ////////////////////////////////////////////////
        ////////////////////////////////////////////////

        return ResponseEntity.ok()
            .header(
                HttpHeaders.SET_COOKIE,
                ResponseCookie
                    .from(accessTokenCookieName)
                    .path(accessTokenPath)
                    .build().toString())
            .header(
                HttpHeaders.SET_COOKIE,
                ResponseCookie
                    .from(refreshTokenCookieName)
                    .path(refreshTokenPath)
                    .build().toString())
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
                ResponseCookie
                    .from(accessTokenCookieName, accessToken)
                    .path(accessTokenPath)
                    .build().toString())
            .header(
                HttpHeaders.SET_COOKIE,
                ResponseCookie
                    .from(refreshTokenCookieName, refreshToken)
                    .path(refreshTokenPath)
                    .build().toString())
            .body(new SimpleResponseBody("refresh success"));
    }
}
