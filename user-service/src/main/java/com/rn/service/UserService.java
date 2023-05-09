package com.rn.service;

import com.rn.payload.UserAuthenticationRequestBody;
import com.rn.payload.UserAuthenticationResponseBody;
import com.rn.payload.UserCreationRequestBody;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;

public interface UserService {
    ResponseEntity<Object> create(UserCreationRequestBody request);
    ResponseEntity<UserAuthenticationResponseBody> authenticate(UserAuthenticationRequestBody request);
    ResponseEntity<UserAuthenticationResponseBody> verifyEmail(String emailVerificationToken);
    ResponseEntity<Object> resendEmailVerification(HttpServletRequest request);
}
