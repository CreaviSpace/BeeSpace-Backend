package com.creavispace.project.domain.jwt.service;

import com.creavispace.project.common.utils.JwtUtil;
import com.creavispace.project.domain.jwt.Entity.Jwt;
import com.creavispace.project.domain.jwt.repository.JwtRepository;
import jakarta.transaction.Transactional;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService  {

    private final JwtRepository jwtRepository;
    @Value("${jwt.secret}")
    private String jwtSecret;

    @Transactional
    @Override
    public void saveRefreshToken(Jwt refreshJwt) {
        initializeDB(refreshJwt.getMemberId());
        System.out.println("JwtServiceImpl.saveRefreshToken");
        jwtRepository.save(refreshJwt);
    }

    @Override
    public Optional<Jwt> findById(String jwt) {
        System.out.println("JwtServiceImpl.findById");
        return jwtRepository.findById(jwt);
    }

    @Override
    public String createRefreshToken(String memberJwt) {
        return JwtUtil.createRefreshToken(memberJwt, jwtSecret);
    }

    @Override
    public void deleteById(String jwt) {
        jwtRepository.deleteById(jwt);
    }

    @Transactional
    private void initializeDB(String memberId) {
        System.out.println("JwtServiceImpl.initializeDB");
        jwtRepository.deleteByMemberId(memberId);
    }
}
