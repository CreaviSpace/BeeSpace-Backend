package com.creavispace.project.domain.member.repository;

import com.creavispace.project.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, String> {
    Optional<Member> findByProvider(String provider);
    @Query("SELECT m FROM Member m WHERE m.id LIKE %:text% OR m.memberNickname LIKE %:text%")
    List<Member> findSearchByIdOrMemberNickname(@Param("text") String text);

}
