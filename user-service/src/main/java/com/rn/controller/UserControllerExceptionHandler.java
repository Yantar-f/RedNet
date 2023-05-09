package com.rn.controller;

import com.rn.payload.ErrorResponseBody;
import com.rn.exception.CookieNotPresentException;
import com.rn.exception.InvalidPasswordOrUsernameException;
import com.rn.exception.InvalidTokenException;
import com.rn.exception.OccupiedValueException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;


@RestControllerAdvice(assignableTypes = UserController.class)
public class UserControllerExceptionHandler {

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

    @ExceptionHandler(value = OccupiedValueException.class)
    public ResponseEntity<ErrorResponseBody> handleOccupiedValueException(
        OccupiedValueException ex,
        HttpServletRequest request
    ) {
        return getDefaultBadRequest(ex,request);
    }

    @ExceptionHandler(value = InvalidPasswordOrUsernameException.class)
    public ResponseEntity<ErrorResponseBody> handleInvalidPasswordOrUsernameException(
        InvalidPasswordOrUsernameException ex,
        HttpServletRequest request
    ) {
        return getDefaultBadRequest(ex,request);
    }

    @ExceptionHandler(value = UsernameNotFoundException.class)
    public ResponseEntity<ErrorResponseBody> handleUsernameNotFoundException(
        UsernameNotFoundException ex,
        HttpServletRequest request
    ) {
        return getDefaultBadRequest(ex,request);
    }

    @ExceptionHandler(value = CookieNotPresentException.class)
    public ResponseEntity<ErrorResponseBody> handleCookieNotPresentException(
        CookieNotPresentException ex,
        HttpServletRequest request
    ) {
        return getDefaultBadRequest(ex,request);
    }

    @ExceptionHandler(value = InvalidTokenException.class)
    public ResponseEntity<ErrorResponseBody> handleInvalidTokenException(
        InvalidTokenException ex,
        HttpServletRequest request
    ) {
        return getDefaultBadRequest(ex,request);
    }




    private ResponseEntity<ErrorResponseBody> getDefaultBadRequest(
        Exception ex,
        HttpServletRequest request
    ) {
        Integer status = HttpStatus.BAD_REQUEST.value();
        Date timestamp = new Date();
        String path = request.getRequestURI();
        List<String> messages = new LinkedList<>();
        messages.add(ex.getMessage());

        return new ResponseEntity<>(
            new ErrorResponseBody(
                status,
                timestamp,
                path,
                messages
            ),
            HttpStatus.BAD_REQUEST
        );
    }
}
