package com.creavispace.project.domain.community.repository;

import com.creavispace.project.domain.admin.dto.DailySummary;
import com.creavispace.project.domain.admin.dto.MonthlySummary;
import com.creavispace.project.domain.admin.dto.YearlySummary;
import com.creavispace.project.domain.community.entity.Community;
import com.creavispace.project.domain.search.entity.SearchResultSet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommunityRepository extends JpaRepository<Community, Long> {
    public Optional<Community> findByIdAndStatusTrue(Long communityId);

    public Page<Community> findAllByStatusTrue(Pageable pageable);


    @Query(value = "SELECT 'COMMUNITY' AS postType, c.id AS postId, c.created_date AS createdDate FROM community c WHERE (c.content LIKE %:text% OR c.title LIKE %:text%) AND c.status = true ORDER BY created_date DESC", nativeQuery = true)
    public Page<SearchResultSet> findCommunitySearchData(String text, Pageable pageable);

    @Query(value = "SELECT 'COMMUNITY' AS postType, c.id AS postId, c.created_date AS createdDate FROM community c WHERE (c.content LIKE %:text% OR c.title LIKE %:text%) AND c.status = true AND c.category = :searchType ORDER BY created_date DESC", nativeQuery = true)
    public Page<SearchResultSet> findCommunityCategorySearchData(String text, String searchType, Pageable pageable);

    Page<Community> findByStatusFalse(Pageable pageRequest);

    Page<Community> findByMemberIdAndStatusTrue(String memberId, Pageable pageRequest);

    List<Community> findByIdIn(List<Long> communityIds);

    Optional<Community> findByIdAndMemberId(Long communityId, String memberId);

    @Query(value = "select * from community c where (:category is null or c.category = :category) and c.status = true and exists ( select 1 from community_hash_tag ch where ch.community_id = c.id and (:hashTag is null or ch.hash_tag_id = :hashTag) )", nativeQuery = true)
    Page<Community> findByCategoryAndHashTagAndStatusTrue(String category, String hashTag, Pageable pageable);

    @Query("SELECT NEW com.creavispace.project.domain.admin.dto.MonthlySummary(YEAR(e.createdDate), MONTH(e.createdDate), COUNT(e)) FROM Community e WHERE YEAR(e.createdDate) = :year GROUP BY YEAR(e.createdDate), MONTH(e.createdDate)")
    List<MonthlySummary> countMonthlySummary(@Param("year") int year);

    @Query("SELECT NEW com.creavispace.project.domain.admin.dto.YearlySummary(YEAR(e.createdDate), COUNT(e)) FROM Community e GROUP BY YEAR(e.createdDate)")
    List<YearlySummary> countYearlySummary();

    @Query("SELECT NEW com.creavispace.project.domain.admin.dto.DailySummary(YEAR(e.createdDate), MONTH(e.createdDate), DAY(e.createdDate), COUNT(e)) FROM Community e WHERE YEAR(e.createdDate) = :year AND MONTH(e.createdDate) = :month GROUP BY YEAR(e.createdDate), MONTH(e.createdDate), DAY(e.createdDate)")
    List<DailySummary> countDailySummary(@Param("year") int year, @Param("month") int month);


}
