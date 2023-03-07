package com.rn.auth.controller;

import com.rn.auth.payload.ErrorResponseBody;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.*;

@RestControllerAdvice(assignableTypes = AuthController.class)
public class AuthControllerExceptionHandler {

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseBody> handleMethodArgumentNotValid(
        MethodArgumentNotValidException ex,
        HttpServletRequest request
    ) {
        Integer status = ex.getStatusCode().value();
        Date timestamp = new Date();
        String path = request.getRequestURI();
        List<String> messages = new LinkedList<>();
        ex.getBindingResult().getFieldErrors().forEach((fieldError) ->
            messages.add(fieldError.getDefaultMessage())
        );

        return new ResponseEntity<>(
            new ErrorResponseBody(
                status,
                timestamp,
                path,
                messages
            ),
            ex.getStatusCode()
        );
    }
}
