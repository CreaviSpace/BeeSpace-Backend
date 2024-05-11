package com.creavispace.project.config.auth.utils;

import com.creavispace.project.domain.jwt.Entity.Jwt;
import com.creavispace.project.domain.member.dto.response.MemberJwtResponseDto;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

@Slf4j
public class JwtUtil {

    public static String createJwt(String memberEmail, String loginType, String memberId,
                                   Boolean fired, String secretKey,
                                   Long expiredTimeStampMs) {
        Claims claims = Jwts.claims();
        claims.put("memberEmail", memberEmail);
        claims.put("loginType", loginType);
        claims.put("memberId", memberId);
        claims.put("fired", fired);
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    //새로운 AccessToken 발급
    public static String refreshJwt(Jwt refreshToken, String secretKey) {
        MemberJwtResponseDto userInfo = getUserInfo(refreshToken.getRefreshToken(), secretKey);
        String memberId = userInfo.memberId();
        String loginType = userInfo.loginType();
        String memberEmail = userInfo.memberEmail();
        boolean fired = userInfo.fired();

        Claims claims = Jwts.claims();
        claims.put("memberEmail", memberEmail);
        claims.put("loginType", loginType);
        claims.put("memberId", memberId);
        claims.put("fired", fired);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60L))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    //리프레시 토큰 생성
    public static String createRefreshToken(String jwt, String secretKey) {
        MemberJwtResponseDto userInfo = getUserInfo(jwt, secretKey);
        String memberId = userInfo.memberId();
        String loginType = userInfo.loginType();
        String memberEmail = userInfo.memberEmail();
        boolean fired = userInfo.fired();

        Claims claims = Jwts.claims();
        claims.put("memberEmail", memberEmail);
        claims.put("loginType", loginType);
        claims.put("memberId", memberId);
        claims.put("fired", fired);
        String refreshToken = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
        return refreshToken;
    }

    public static MemberJwtResponseDto getUserInfo(String token, String secretKey) {
        Claims body = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
        String memberEmail = body.get("memberEmail", String.class);
        String loginType = body.get("loginType", String.class);
        String memberId = body.get("memberId", String.class);
        Boolean fired = body.get("fired", Boolean.class);
        return new MemberJwtResponseDto(memberEmail, loginType, memberId, fired);
    }

    public static boolean isExpired(String token, String secretKey) {
        log.info("JwtUtils.isExpired?");
        try {
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
        } catch (ExpiredJwtException e) {
            return true;
        }
        return false;
    }
}
