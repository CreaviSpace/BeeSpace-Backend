package com.creavispace.project.domain.auth.jwt.repository;

import com.creavispace.project.domain.auth.jwt.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {

    @Transactional
    void deleteByMemberId(String memberId);

    @Transactional
    void deleteByRefreshToken(String refreshToken);

    Optional<RefreshToken> findByRefreshToken(String refresh);

}
