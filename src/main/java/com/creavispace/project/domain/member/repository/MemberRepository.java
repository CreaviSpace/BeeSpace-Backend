package com.creavispace.project.domain.member.repository;

import com.creavispace.project.domain.member.entity.Member;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, String> {
    boolean existsByMemberEmail(String email);
    Optional<Member> findByMemberEmail(String email);
    Optional<Member> findByMemberEmailAndMemberNameAndLoginId(String email, String loginId, String name);
    Optional<Member> findByMemberEmailAndLoginTypeAndId(String memberEmail, String loginType, String memberId);

    @Query("SELECT m FROM Member m WHERE m.memberNickname LIKE %:keyword% OR m.id LIKE %:keyword%")
    List<Member> findByMemberNicknameOrIdTagContaining(String keyword);
    Optional<Member> findByLoginId(String loginId);
    Optional<Member> findById(String memberId);


    List<Member> findBy(Pageable pageable);

}
