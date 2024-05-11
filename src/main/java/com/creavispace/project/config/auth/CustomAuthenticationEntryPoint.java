package com.creavispace.project.config.auth;

import com.creavispace.project.config.auth.dto.RefreshJwtDto;
import com.creavispace.project.config.auth.utils.JwtUtil;
import com.creavispace.project.domain.jwt.Entity.Jwt;
import com.creavispace.project.domain.jwt.service.JwtService;
import com.creavispace.project.domain.member.dto.response.MemberJwtResponseDto;
import com.creavispace.project.global.exception.CreaviCodeException;
import com.creavispace.project.global.exception.GlobalErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;


@Slf4j
@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final JwtService jwtService;
    private final String jwtSecret;


    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        ObjectMapper objectMapper = new ObjectMapper();
        System.out.println("CustomAuthenticationEntryPoint.commence");
        RefreshJwtDto refreshJwt = refreshToken(request);

        String jsonString = objectMapper.writeValueAsString(refreshJwt);

        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_ACCEPTED);
        response.setCharacterEncoding("utf-8");
        response.getWriter().write(jsonString);
    }

    private RefreshJwtDto refreshToken(HttpServletRequest request) {
        String expiredToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        Jwt refreshToken = checkRefreshToken(expiredToken);
        String refreshJwt = JwtUtil.refreshJwt(refreshToken, jwtSecret);
        MemberJwtResponseDto userInfo = JwtUtil.getUserInfo(refreshJwt, jwtSecret);
        RefreshJwtDto refreshJwtDto = new RefreshJwtDto(refreshJwt, userInfo.memberId());
        reloadRefreshToken(refreshJwt, userInfo.memberId());
        return refreshJwtDto;
    }

    private Jwt checkRefreshToken(String expiredToken) {
        log.info("리프레시 토큰 조회 jwt = {}", expiredToken);
        Optional<Jwt> refreshTokenOps = jwtService.findById(expiredToken);
        if (refreshTokenOps.isEmpty()) {
            log.info("리프레시 토큰이 존재하지 않습니다.");
            throw new CreaviCodeException(GlobalErrorCode.NOT_PERMISSMISSION);
        }
        if (JwtUtil.isExpired(refreshTokenOps.get().getRefreshToken(), jwtSecret)) {
            log.info("리프레시 토큰이 만료 되었습니다.");
            throw new CreaviCodeException(GlobalErrorCode.NOT_PERMISSMISSION);
        }
        log.info("리프레시 토큰이 존재합니다. 새로운 AccessToken 발급");
        Jwt refreshToken = refreshTokenOps.get();
        return refreshToken;
    }

    //리프레시 토큰 다시 생성
    private void reloadRefreshToken(String jwt, String memberId) {
        String refreshToken = jwtService.createRefreshToken(jwt);
        Jwt refreshJwt = new Jwt(jwt, refreshToken, memberId);
        jwtService.saveRefreshToken(refreshJwt);
    }
}
