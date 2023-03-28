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


    public static final String SIGN_UP_PATH = "/signup";
    public static final String SIGN_IN_PATH = "/signin";
    public static final String SIGN_OUT_PATH = "/signout";
    public static final String REFRESH_TOKEN_PATH = "/refresh-token";
    public static final String VERIFY_EMAIL_PATH = "/verify-email";
    public static final String RESEND_EMAIL_VERIFICATION_PATH = "/resend-email-verification";
    private final AuthService authService;



    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }




    @PostMapping(SIGN_UP_PATH)
    public ResponseEntity<?> signUp(@RequestBody @Valid SignUpRequestBody request) {
        return authService.signUp(request);
    }

    @PostMapping(SIGN_IN_PATH)
    public ResponseEntity<?> signIn(@RequestBody @Valid SignInRequestBody request) {
        return authService.signIn(request);
    }

    @PostMapping(SIGN_OUT_PATH)
    public ResponseEntity<?> signOut(HttpServletRequest request) {
        return authService.signOut(request);
    }

    @PostMapping(REFRESH_TOKEN_PATH)
    public ResponseEntity<?> refreshToken(HttpServletRequest request) {
        return authService.refreshToken(request);
    }

    @GetMapping(VERIFY_EMAIL_PATH)
    public ResponseEntity<SignInResponseBody> verifyEmail(@RequestParam(name = "token") String token) {
        return authService.verifyEmail(token);
    }

    @PostMapping(RESEND_EMAIL_VERIFICATION_PATH)
    public ResponseEntity<?> resendEmailVerification(HttpServletRequest request) {
        return authService.resendEmailVerification(request);
    }
}