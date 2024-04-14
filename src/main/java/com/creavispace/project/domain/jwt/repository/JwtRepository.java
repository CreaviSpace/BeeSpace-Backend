package com.creavispace.project.domain.jwt.repository;

import com.creavispace.project.domain.jwt.Entity.Jwt;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JwtRepository extends JpaRepository<Jwt, String> {
    List<Jwt> findByMemberId(String memberId);

    void deleteByMemberId(String memberId);

    Optional<Jwt> findById(String jwt);
}
