package com.creavispace.project.config.auth.utils;

import com.creavispace.project.domain.member.dto.response.MemberJwtResponseDto;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

@Slf4j
public class JwtUtil {

    public static String createJwt(String memberEmail, String loginType, String memberIdTag, String secretKey, Long expiredTimeStampMs) {
        Claims claims = Jwts.claims();
        claims.put("memberEmail", memberEmail);
        claims.put("loginType", loginType);
        claims.put("memberIdTag", memberIdTag);
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiredTimeStampMs))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public static MemberJwtResponseDto getUserInfo(String token, String secretKey) {
        Claims body = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
        String memberEmail = body.get("memberEmail", String.class);
        String loginType = body.get("loginType", String.class);
        String memberIdTag = body.get("memberIdTag", String.class);
        return new MemberJwtResponseDto(memberEmail, loginType, memberIdTag);
    }

    public static boolean isExpired(String token, String secretKey) {
        log.info("JwtUtils.isExpired");
        try {
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
        } catch (ExpiredJwtException e) {
            return true;
        }
        return false;
    }
}
