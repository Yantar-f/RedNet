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
    ResponseEntity<SimpleResponseBody> refresh(HttpServletRequest request);
    ResponseEntity<SignInResponseBody> verify(String emailVerificationToken);
    ResponseEntity<Object> resendVerification(HttpServletRequest request);
}
