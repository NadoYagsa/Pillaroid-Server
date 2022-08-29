package com.nadoyagsa.pillaroid.jwt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.nadoyagsa.pillaroid.common.exception.UnauthorizedException;

@Component
public class AuthInterceptor implements HandlerInterceptor {
    private final AuthTokenProvider authTokenProvider;

    @Autowired
    public AuthInterceptor(AuthTokenProvider authTokenProvider) {
        this.authTokenProvider = authTokenProvider;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String token = request.getHeader("authorization");
        if (token != null && authTokenProvider.validateToken(token)) {
            return true;
        } else {
            throw UnauthorizedException.UNAUTHORIZED_USER;
        }
    }
}
