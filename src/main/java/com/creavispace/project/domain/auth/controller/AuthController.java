package com.creavispace.project.domain.auth.controller;

import com.creavispace.project.common.dto.response.SuccessResponseDto;
import com.creavispace.project.common.exception.CreaviCodeException;
import com.creavispace.project.domain.auth.dto.response.ReissueResponseDto;
import com.creavispace.project.domain.auth.jwt.JWTService;
import com.creavispace.project.domain.auth.jwt.JWTUtil;
import com.creavispace.project.domain.auth.jwt.entity.RefreshToken;
import com.creavispace.project.domain.auth.jwt.repository.RefreshTokenRepository;
import com.creavispace.project.domain.member.entity.Member;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.time.Instant;
import java.util.NoSuchElementException;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final JWTUtil jwtUtil;

    private final RefreshTokenRepository refreshTokenRepository;

    private final JWTService jwtService;

    @PostMapping("/reissue")
    public ResponseEntity<SuccessResponseDto<ReissueResponseDto>> reissue(HttpServletRequest request, HttpServletResponse response){
        String refresh = null;
        Cookie[] cookies = request.getCookies();

        for (Cookie cookie : cookies) {
            if(cookie.getName().equals("refresh")) {
                refresh = cookie.getValue();
            }
        }

        if(refresh == null){
            throw new CreaviCodeException("refresh token null", 400);
        }

        if(jwtUtil.isExpired(refresh)){
            throw new CreaviCodeException("refresh token expired", 400);
        }

        // 토큰이 refresh 인지 확인 (발급시 페이로드에 명시)
        String category = jwtUtil.getCategory(refresh);

        if(!category.equals("refresh")){
            throw new CreaviCodeException("invalid refresh token", 400);
        }

        // DB에 저장되어 있는지 확인
        RefreshToken refreshToken = refreshTokenRepository.findByRefreshToken(refresh).orElseThrow(() -> new NoSuchElementException("invalid refresh token"));

        // 토큰의 회원 정보 조회
        Member member = refreshToken.getMember();

        String memberId = member.getId();
        String role = member.getRole().name();

        // new JWT 생성
        String newAccess = jwtUtil.createJwt("access", memberId, role, 60 * 60 * 1000L);
        String newRefresh = jwtUtil.createJwt("refresh", null, null, 24 * 60 * 60 * 1000L);

        // 기존의 Refresh 토큰 삭제 후 새 Refresh 토큰 저장
        refreshTokenRepository.deleteByRefreshToken(refresh);
        jwtService.addRefreshToken(memberId, newRefresh, 24 * 60 * 60 * 1000L);

        // responseBody data toDto
        ReissueResponseDto data = new ReissueResponseDto(newAccess);

        // response 에 쿠키(new refresh 토큰) 추가
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, createCookie("refresh", newRefresh, Duration.ofHours(1)));

        return ResponseEntity.ok().headers(headers).body(new SuccessResponseDto<>(true, "토큰 재생성이 완료되었습니다.", data));


    }

    private String createCookie(String key, String value, Duration duration) {

        Instant now = Instant.now();
        Instant expiresRefresh = now.plus(duration);

        ResponseCookie cookie = ResponseCookie.from(key, value)
                .httpOnly(true)
                .maxAge(Duration.between(now, expiresRefresh))
                .secure(true)
                .domain("beespace.vercel.app")
                .path("/")
                .sameSite("None")
                .build();

        return cookie.toString();
    }
}
