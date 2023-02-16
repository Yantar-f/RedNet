package com.rn.auth.service;

import com.rn.auth.model.payload.SignInRequest;
import com.rn.auth.model.payload.SignInResponse;
import com.rn.auth.model.payload.SignUpRequest;

public interface AuthService {

    SignInResponse signUp(SignUpRequest request);
    SignInResponse signIn(SignInRequest request);
}
