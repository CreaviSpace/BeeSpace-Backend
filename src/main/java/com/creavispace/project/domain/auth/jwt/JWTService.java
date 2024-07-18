package com.creavispace.project.domain.auth.jwt;

import com.creavispace.project.domain.auth.jwt.entity.RefreshToken;
import com.creavispace.project.domain.auth.jwt.repository.RefreshTokenRepository;
import com.creavispace.project.domain.member.entity.Member;
import com.creavispace.project.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class JWTService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final MemberRepository memberRepository;

    public void addRefreshToken(String memberId, String refresh, Long expiredMs){

        Member member = memberRepository.findById(memberId).orElseThrow(() -> new NoSuchElementException("리프레시 토큰에 저장할 맴버가 존재하지 않습니다."));

        LocalDateTime date = LocalDateTime.now().plus(expiredMs, ChronoUnit.MILLIS);

        RefreshToken refreshToken = RefreshToken.builder()
                .refreshToken(refresh)
                .member(member)
                .expiredTime(date)
                .build();

        refreshTokenRepository.save(refreshToken);
    }
}
