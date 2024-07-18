package com.creavispace.project.domain.auth.oauth2;

import com.creavispace.project.domain.auth.jwt.JWTService;
import com.creavispace.project.domain.auth.jwt.JWTUtil;
import com.creavispace.project.domain.auth.oauth2.dto.CustomOAuth2User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
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
        String access = jwtUtil.createJwt("access", memberId, role, 60 * 60 * 1000L);
        String refresh = jwtUtil.createJwt("refresh", null, null, 24 * 60 * 60 * 1000L);

        // refresh 토큰 저장
        jwtService.addRefreshToken(memberId, refresh, 24 * 60 * 60 * 1000L);
        System.out.println(access);
        response.addHeader("Set-Cookie", createCookie("refresh", refresh, Duration.ofHours(1)));
        response.sendRedirect("https://beespace.vercel.app/login");
    }

    private String createCookie(String key, String value, Duration duration) {

        Instant now = Instant.now();
        Instant expiresRefresh = now.plus(duration);

        ResponseCookie cookie = ResponseCookie.from(key, value)
                .httpOnly(true)
                .maxAge(Duration.between(now, expiresRefresh))
//                .secure(true)
                .domain("beespace.vercel.app")
                .path("/")
//                .sameSite("None")
                .build();

        return cookie.toString();
    }

}
