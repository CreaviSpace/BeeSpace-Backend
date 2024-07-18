package com.creavispace.project.domain.auth.jwt;

import com.creavispace.project.domain.auth.oauth2.dto.CustomOAuth2User;
import com.creavispace.project.domain.member.entity.Member;
import com.creavispace.project.domain.member.entity.Role;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;

@Slf4j
@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 헤더에서 authorization 키에 담긴 토큰을 꺼냄(access 토큰)
        final String accessToken = request.getHeader(HttpHeaders.AUTHORIZATION);

        // 토큰이 없다면 다음 필터로 넘김
        if (accessToken == null) {
            filterChain.doFilter(request, response);
            return;
        }

        // 토큰 만료 여부 확인, 만료시 다음 필터로 넘기지 않음
        if(jwtUtil.isExpired(accessToken)){
            //response body
            PrintWriter writer = response.getWriter();
            writer.print("access token expired");

            //response status code
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        // 토큰 종류가 access 맞는지 확인 (발급시 페이로드에 명시)
        String category = jwtUtil.getCategory(accessToken);

        if (!category.equals("access")) {

            //response body
            PrintWriter writer = response.getWriter();
            writer.print("invalid access token");

            //response status code
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        // memberId, role 값을 획득
        String memberId = jwtUtil.getId(accessToken);
        String role = jwtUtil.getRole(accessToken);

        // 토큰에 저장된 회원정보 생성
        Member member = Member.builder()
                .id(memberId)
                .role(Role.fromKey(role))
                .build();

        // customOAuth2User 에 회원 정보 객체 담기
        CustomOAuth2User customOAuth2User = new CustomOAuth2User(member);

        // 스프링 스큐리티 인증 토큰 생성
        Authentication authToken = new UsernamePasswordAuthenticationToken(customOAuth2User, null, customOAuth2User.getAuthorities());
        // 세션에 사용자 등록
        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);

    }
}
