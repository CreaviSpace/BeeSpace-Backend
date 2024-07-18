package com.creavispace.project.domain.auth.oauth2;

import com.creavispace.project.domain.auth.jwt.JWTUtil;
import com.creavispace.project.domain.auth.jwt.JWTService;
import com.creavispace.project.domain.auth.oauth2.dto.CustomOAuth2User;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

@Slf4j
@RequiredArgsConstructor
public class LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JWTUtil jwtUtil;
    private final JWTService jwtService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        // 유저 정보
        CustomOAuth2User customOAuth2User = (CustomOAuth2User) authentication.getPrincipal();

        String memberId = customOAuth2User.getId();

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();
        String role = auth.getAuthority();

        // 토큰 생성
        String refresh = jwtUtil.createJwt("refresh", memberId, role, 24 * 60 * 60 * 1000L);

        // refresh 토큰 저장
        jwtService.addRefreshToken(memberId, refresh, 24 * 60 * 60 * 1000L);

        response.addCookie(createCookie("refresh", refresh, 24*60*60));
        response.sendRedirect("https://beespace.vercel.app/login");
    }

    private Cookie createCookie(String key, String value, int expiry) {

        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(expiry);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setHttpOnly(true);

        return cookie;
    }

}
