package com.rn.auth.service;

import com.rn.auth.payload.SimpleResponseBody;
import com.rn.auth.payload.SignInRequestBody;
import com.rn.auth.payload.SignInResponseBody;
import com.rn.auth.payload.SignUpRequestBody;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;

public interface AuthService {
    ResponseEntity<Object> signUp(SignUpRequestBody request);
    ResponseEntity<SignInResponseBody> signIn(SignInRequestBody request);
    ResponseEntity<SimpleResponseBody> signOut(HttpServletRequest request);
    ResponseEntity<SimpleResponseBody> refresh(HttpServletRequest request);

    ResponseEntity<SignInResponseBody> verify(String token);
}
