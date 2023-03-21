package com.rn.controller;

import com.rn.payload.SignInRequestBody;
import com.rn.payload.SignInResponseBody;
import com.rn.payload.SignUpRequestBody;

import com.rn.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping ("/api/auth")
public class AuthController {

    private final AuthService authService;




    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }




    @PostMapping ("/signup")
    public ResponseEntity<?> signUp(@RequestBody @Valid SignUpRequestBody request) {
        return authService.signUp(request);
    }

    @PostMapping("/signin")
    public ResponseEntity<?> signIn(@RequestBody @Valid SignInRequestBody request) {
        return authService.signIn(request);
    }

    @PostMapping("/signout")
    public ResponseEntity<?> signOut(HttpServletRequest request) {
        return authService.signOut(request);
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(HttpServletRequest request) {
        return authService.refresh(request);
    }

    @GetMapping("/verify")
    public ResponseEntity<SignInResponseBody> verify(@RequestParam(name = "token") String token) {
        return authService.verify(token);
    }

    @PostMapping("/resend-verification")
    public ResponseEntity<?> resendVerification(HttpServletRequest request) {
        return authService.resendVerification(request);
    }
}