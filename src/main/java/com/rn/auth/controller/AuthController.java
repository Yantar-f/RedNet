package com.rn.auth.controller;

import com.rn.auth.model.payload.SignInRequest;
import com.rn.auth.model.payload.SignInResponse;
import com.rn.auth.model.payload.SignUpRequest;

import com.rn.auth.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;




    @Autowired
    public AuthController(AuthService authService){
        this.authService = authService;
    }




    @PostMapping("/signup")
    public ResponseEntity<SignInResponse> signUp(@RequestBody SignUpRequest request) {
        return ResponseEntity.ok(authService.signUp(request));
    }

    @PostMapping("/signin")
    public ResponseEntity<SignInResponse> signIn(@RequestBody SignInRequest request) {
        return ResponseEntity.ok(authService.signIn(request));
    }
}