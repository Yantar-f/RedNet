package com.rn.controller;

import com.rn.payload.UserAuthenticationRequestBody;
import com.rn.payload.UserAuthenticationResponseBody;
import com.rn.payload.UserCreationRequestBody;

import com.rn.service.UserService;
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

import static com.rn.controller.UserController.MAIN_PATH;


@RestController
@RequestMapping (MAIN_PATH)
public class UserController {

    public static final String MAIN_PATH = "/api/user";
    public static final String CREATE_USER_PATH = "/create";
    public static final String AUTHENTICATE_USER_PATH = "/authenticate";
    public static final String VERIFY_EMAIL_PATH = "/verify-email";
    public static final String RESEND_EMAIL_VERIFICATION_PATH = "/resend-email-verification";
    private final UserService userService;



    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }




    @PostMapping(CREATE_USER_PATH)
    public ResponseEntity<?> create(@RequestBody @Valid UserCreationRequestBody request) {
        return userService.create(request);
    }

    @PostMapping(AUTHENTICATE_USER_PATH)
    public ResponseEntity<?> authenticate(@RequestBody @Valid UserAuthenticationRequestBody request) {
        return userService.authenticate(request);
    }

    @GetMapping(VERIFY_EMAIL_PATH)
    public ResponseEntity<UserAuthenticationResponseBody> verifyEmail(@RequestParam(name = "token") String token) {
        return userService.verifyEmail(token);
    }

    @PostMapping(RESEND_EMAIL_VERIFICATION_PATH)
    public ResponseEntity<Object> resendEmailVerification(HttpServletRequest request) {
        return userService.resendEmailVerification(request);
    }

}