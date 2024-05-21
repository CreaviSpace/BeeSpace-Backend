package com.creavispace.project.config;

import com.creavispace.project.domain.jwt.service.JwtService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

@RequiredArgsConstructor
public class LogoutHandler implements LogoutSuccessHandler {
    private final JwtService jwtService;
    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {
        System.out.println("------------------로그아웃------------------");
        String jwt = request.getHeader(HttpHeaders.AUTHORIZATION);
        jwtService.deleteById(jwt);
    }
}
