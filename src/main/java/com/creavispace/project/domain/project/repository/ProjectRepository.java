package com.creavispace.project.domain.project.repository;

import com.creavispace.project.domain.admin.dto.DailySummary;
import com.creavispace.project.domain.admin.dto.MonthlySummary;
import com.creavispace.project.domain.admin.dto.YearlySummary;
import com.creavispace.project.common.dto.type.ProjectCategory;
import com.creavispace.project.domain.project.entity.Project;
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
public interface ProjectRepository extends JpaRepository<Project, Long>{
    public List<Project> findTop6ByStatusTrueOrderByWeekViewCountDesc();

    public Page<Project> findAllByStatusTrue(Pageable pageable);

    @Query(value = "select * from project where status = true and (:category is null or category = :category)", nativeQuery = true)
    public Page<Project> findAllByStatusTrueAndCategory(String category, Pageable pageable);

    public Optional<Project> findByIdAndStatusTrue(Long projectId);

    public Boolean existsByIdAndMemberId(Long projectId, String memberId);

    @Query(value = "SELECT 'PROJECT' AS postType, p.id AS postId, p.created_date AS createdDate FROM project p WHERE (p.content LIKE %:text% OR p.title LIKE %:text%) AND p.status = true ORDER BY createdDate DESC", nativeQuery = true)
    public Page<SearchResultSet> findProjectSearchData(@Param("text") String text, Pageable pageable);

    @Query(value = "SELECT 'PROJECT' AS postType, p.id AS postId, p.created_date AS createdDate FROM project p WHERE (p.content LIKE %:text% OR p.title LIKE %:text%) AND p.status = true AND p.category = :searchType ORDER BY createdDate DESC", nativeQuery = true)
    public Page<SearchResultSet> findProjectCategorySearchData(@Param("text") String text, @Param("searchType") String searchType, Pageable pageable);

    @Query(value = "SELECT postType, postId, createdDate FROM ( " +
            "SELECT 'PROJECT' AS postType, p.id AS postId, p.created_date AS createdDate FROM project p WHERE (p.content LIKE %:text% OR p.title LIKE %:text%) AND p.status = true " +
            "UNION ALL " +
            "SELECT 'RECRUIT' AS postType, r.id AS postId, r.created_date AS createdDate FROM recruit r WHERE (r.content LIKE %:text% OR r.title LIKE %:text%) AND r.status = true " +
            "UNION ALL " +
            "SELECT 'COMMUNITY' AS postType, c.id AS postId, c.created_date AS createdDate FROM community c WHERE (c.content LIKE %:text% OR c.title LIKE %:text%) AND c.status = true " +
            ") AS subquery_alias " +
            "ORDER BY createdDate DESC",
            countQuery = "SELECT COUNT(*) FROM ( " +
                    "SELECT 'PROJECT' AS postType, p.id AS postId, p.created_date AS createdDate FROM project p WHERE (p.content LIKE %:text% OR p.title LIKE %:text%) AND p.status = true " +
                    "UNION ALL " +
                    "SELECT 'RECRUIT' AS postType, r.id AS postId, r.created_date AS createdDate FROM recruit r WHERE (r.content LIKE %:text% OR r.title LIKE %:text%) AND r.status = true " +
                    "UNION ALL " +
                    "SELECT 'COMMUNITY' AS postType, c.id AS postId, c.created_date AS createdDate FROM community c WHERE (c.content LIKE %:text% OR c.title LIKE %:text%) AND c.status = true " +
                    ") AS subquery_alias",
            nativeQuery = true)
    public Page<SearchResultSet> findIntegratedSearchData(@Param("text") String text, Pageable pageable);

    Page<Project> findByStatusFalse(Pageable pageable);

    Page<Project> findAllByStatusTrueAndCategoryAndMemberId(ProjectCategory projectCategory, String memberId, Pageable pageRequest);

    Page<Project> findAllByStatusTrueAndMemberId(String memberId, Pageable pageRequest);

    List<Project> findByIdIn(List<Long> projectIds);

    Optional<Project> findByIdAndMemberId(Long projectId, String memberId);

    @Query("SELECT NEW com.creavispace.project.domain.admin.dto.MonthlySummary(YEAR(e.createdDate), MONTH(e.createdDate), COUNT(e)) FROM Project e WHERE YEAR(e.createdDate) = :year GROUP BY YEAR(e.createdDate), MONTH(e.createdDate)")
    List<MonthlySummary> countMonthlySummary(@Param("year") int year);

    @Query("SELECT NEW com.creavispace.project.domain.admin.dto.YearlySummary(YEAR(e.createdDate), COUNT(e)) FROM Project e GROUP BY YEAR(e.createdDate)")
    List<YearlySummary> countYearlySummary();

    @Query("SELECT NEW com.creavispace.project.domain.admin.dto.DailySummary(YEAR(e.createdDate), MONTH(e.createdDate), DAY(e.createdDate), COUNT(e)) FROM Project e WHERE YEAR(e.createdDate) = :year AND MONTH(e.createdDate) = :month GROUP BY YEAR(e.createdDate), MONTH(e.createdDate), DAY(e.createdDate)")
    List<DailySummary> countDailySummary(@Param("year") int year, @Param("month") int month);


}
