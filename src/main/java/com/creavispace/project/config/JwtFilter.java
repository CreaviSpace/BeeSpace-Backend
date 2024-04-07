package com.creavispace.project.config;

import com.creavispace.project.config.auth.utils.JwtUtil;
import com.creavispace.project.domain.member.dto.response.MemberJwtResponseDto;
import com.creavispace.project.domain.member.entity.Member;
import com.creavispace.project.domain.member.service.MemberService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
public class JwtFilter extends OncePerRequestFilter {
    private final MemberService memberService;

    private String jwtSecret;


    public JwtFilter(MemberService memberService, String jwtSecret) {
        this.memberService = memberService;
        this.jwtSecret = jwtSecret;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        final String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        request.getHeaderNames().asIterator().forEachRemaining(System.out::println);

        log.info("authorization = {}" , authorization);
        log.info("Headers.logintype = {}", request.getHeader("logintype"));


        //authorization 헤더가 비어있으면 바로 리턴
        //eyJhbGciOiJIUzI1NiJ9 -> HS256 으로 인코딩 된 토큰이 아니면 리턴
        if (authorization == null || !authorization.startsWith("eyJhbGciOiJIUzI1NiJ9")) {
            log.info("인증되지 않은 사용자 입니다. authorization = {}", authorization);
            filterChain.doFilter(request, response);
            return;
        }

        if (JwtUtil.isExpired(authorization, jwtSecret)) {
            log.info("만료된 토큰 입니다. = {}", authorization);
            filterChain.doFilter(request, response);
            return;
        }

        log.info("인증 성공 authorization = {}", authorization);

        MemberJwtResponseDto responseDto = JwtUtil.getUserInfo(authorization, jwtSecret);
        String memberIdTag = responseDto.memberIdTag();
        String memberEmail = responseDto.memberEmail();
        String loginType = responseDto.loginType();

        Member member = memberService.findByMemberIdTag(memberIdTag).orElseThrow();
        log.info("로그인 한 사용자 = {}, 로그인 타입 = {}",memberEmail, loginType);

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(member.getId(), loginType, List.of(new SimpleGrantedAuthority(member.getRoleKey())));

        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        filterChain.doFilter(request, response);
    }
}
