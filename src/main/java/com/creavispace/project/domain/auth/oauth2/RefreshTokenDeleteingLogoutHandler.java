package com.creavispace.project.domain.auth.oauth2;

import com.creavispace.project.domain.auth.jwt.repository.RefreshTokenRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;

@RequiredArgsConstructor
public class RefreshTokenDeleteingLogoutHandler implements LogoutHandler {
    
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {

        Cookie[] cookies = request.getCookies();
        if(cookies != null){
            for(Cookie cookie : cookies){
                if(cookie.getName().equals("refresh")){
                    String refresh = cookie.getValue();
                    refreshTokenRepository.deleteByRefreshToken(refresh);
                }
            }
        }
    }
}
