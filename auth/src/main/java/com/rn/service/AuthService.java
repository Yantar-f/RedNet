package com.rn.service;

import com.rn.payload.SimpleResponseBody;
import com.rn.payload.SignInRequestBody;
import com.rn.payload.SignInResponseBody;
import com.rn.payload.SignUpRequestBody;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;

public interface AuthService {
    ResponseEntity<Object> signUp(SignUpRequestBody request);
    ResponseEntity<SignInResponseBody> signIn(SignInRequestBody request);
    ResponseEntity<SimpleResponseBody> signOut(HttpServletRequest request);
    ResponseEntity<SimpleResponseBody> refreshToken(HttpServletRequest request);
    ResponseEntity<SignInResponseBody> verifyEmail(String emailVerificationToken);
    ResponseEntity<Object> resendEmailVerification(HttpServletRequest request);
    ResponseEntity<Boolean> verifyRequest(HttpServletRequest request);
}
