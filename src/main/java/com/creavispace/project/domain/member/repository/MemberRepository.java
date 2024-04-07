package com.creavispace.project.domain.member.repository;

import com.creavispace.project.domain.member.entity.Member;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MemberRepository extends JpaRepository<Member, Long> {
    boolean existsByMemberEmail(String email);
    Optional<Member> findByMemberEmail(String email);
    Optional<Member> findByMemberEmailAndMemberNameAndLoginId(String email, String loginId, String name);
    Optional<Member> findByMemberEmailAndLoginTypeAndId(String memberEmail, String loginType, Long memberId);

    @Query("SELECT m FROM Member m WHERE m.memberNickname LIKE %:keyword% OR m.idTag LIKE %:keyword%")
    List<Member> findByMemberNicknameOrIdTagContaining(String keyword);
    Optional<Member> findByLoginId(String loginId);
    Optional<Member> findByIdTag(String memberIdTag);


    Page<Member> findAll(Pageable pageable);

}
