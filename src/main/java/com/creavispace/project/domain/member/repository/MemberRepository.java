package com.creavispace.project.domain.member.repository;

import com.creavispace.project.domain.admin.dto.DailySummary;
import com.creavispace.project.domain.admin.dto.MonthlySummary;
import com.creavispace.project.domain.admin.dto.YearlySummary;
import com.creavispace.project.domain.member.entity.Member;
import java.time.LocalDateTime;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.query.Param;

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

    List<Member> findByIdIn(List<String> collect);

    Optional<Member> findByLoginIdAndLoginType(String loginId, String loginType);

    @Query("SELECT NEW com.creavispace.project.domain.admin.dto.MonthlySummary(YEAR(e.createdDate), MONTH(e.createdDate), COUNT(e)) FROM Member e WHERE YEAR(e.createdDate) = :year GROUP BY YEAR(e.createdDate), MONTH(e.createdDate)")
    List<MonthlySummary> countMonthlySummary(@Param("year") int year);

    @Query("SELECT NEW com.creavispace.project.domain.admin.dto.YearlySummary(YEAR(e.createdDate), COUNT(e)) FROM Member e GROUP BY YEAR(e.createdDate)")
    List<YearlySummary> countYearlySummary();

    @Query("SELECT NEW com.creavispace.project.domain.admin.dto.DailySummary(YEAR(e.createdDate), MONTH(e.createdDate), DAY(e.createdDate), COUNT(e)) FROM Member e WHERE YEAR(e.createdDate) = :year AND MONTH(e.createdDate) = :month GROUP BY YEAR(e.createdDate), MONTH(e.createdDate), DAY(e.createdDate)")
    List<DailySummary> countDailySummary(@Param("year") int year, @Param("month") int month);

}
