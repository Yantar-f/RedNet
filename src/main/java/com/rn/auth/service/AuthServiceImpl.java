package com.rn.auth.service;

import com.rn.auth.model.UserDetailsImpl;
import com.rn.auth.model.entity.EnumRole;
import com.rn.auth.model.entity.Role;
import com.rn.auth.model.entity.User;
import com.rn.auth.model.payload.SignInRequest;
import com.rn.auth.model.payload.SignInResponse;
import com.rn.auth.model.payload.SignUpRequest;
import com.rn.auth.repository.RoleRepository;
import com.rn.auth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthTokenService authTokenService;
    private final AuthenticationManager authenticationManager;




    @Autowired
    private AuthServiceImpl(
        UserRepository userRepository,
        RoleRepository roleRepository,
        PasswordEncoder passwordEncoder,
        AuthTokenService authTokenService,
        AuthenticationManager authenticationManager
    ) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.authTokenService = authTokenService;
        this.authenticationManager = authenticationManager;
    }




    @Override
    public SignInResponse signUp(SignUpRequest request) {
        User user = new User();
        Role role = roleRepository
            .findByDesignation(EnumRole.USER)
            .orElseThrow();

        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRoles(Set.of(role));

        userRepository.save(user);
        String token = authTokenService.generateToken(new UserDetailsImpl(user));
        return new SignInResponse(token);
    }

    @Override
    public SignInResponse signIn(SignInRequest request) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.getUsername(),
                request.getPassword()
            )
        );

        User user = userRepository
            .findByUsername(request.getUsername())
            .orElseThrow();
        String token = authTokenService.generateToken(new UserDetailsImpl(user));

        return new SignInResponse(token);
    }
}
