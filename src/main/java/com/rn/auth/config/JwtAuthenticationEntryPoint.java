package com.rn.auth.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import com.rn.auth.payload.ErrorResponseBody;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(
        HttpServletRequest request,
        HttpServletResponse response,
        AuthenticationException authException
    ) throws IOException, ServletException {
        Integer status = HttpStatus.UNAUTHORIZED.value();
        Date timestamp = new Date();
        DateFormat dateFormat = new StdDateFormat();
        String path = request.getRequestURI();
        List<String> messages = new LinkedList<>();
        messages.add(authException.getMessage());

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(status);

        final ObjectMapper mapper = new ObjectMapper();
        mapper
            .setDateFormat(dateFormat)
            .writeValue(
                response.getOutputStream(),
                new ErrorResponseBody(
                    status,
                    timestamp,
                    path,
                    messages
                )
            );
    }
}
