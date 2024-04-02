package com.creavispace.project.domain.project.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.creavispace.project.domain.project.entity.Project;
import com.creavispace.project.domain.member.entity.Member;
import com.creavispace.project.domain.search.entity.SearchResultSet;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long>{
    public List<Project> findTop6ByStatusTrueOrderByWeekViewCountDesc();
    public Page<Project> findAllByStatusTrue(Pageable pageable);
    public Page<Project> findAllByStatusTrueAndCategory(String category, Pageable pageable);
    public Optional<Project> findByIdAndStatusTrue(Long projectId);
    public Boolean existsByIdAndMemberId(Long projectId, Long memberId);

    @Query(value = "SELECT 'PROJECT' AS postType, p.id AS postId, p.created_date AS createdDate FROM project p WHERE (p.content LIKE %:text% OR p.title LIKE %:text%) AND p.status = true ORDER BY createdDate DESC", nativeQuery = true)
    public Page<SearchResultSet> findProjectSearchData(@Param("text") String text, Pageable pageable);

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

    Page<Project> findAllByStatusTrueAndMemberIdOrderByCreatedDateAsc(Pageable pageRequest, Member member);
    Page<Project> findAllByStatusTrueAndMemberIdOrderByCreatedDateDesc(Pageable pageRequest, Member member);
    Page<Project> findAllByStatusTrueAndMemberIdOrderByCreatedDateAsc(Pageable pageRequest, Long memberId);
    Page<Project> findAllByStatusTrueAndMemberIdOrderByCreatedDateDesc(Pageable pageRequest, Long memberId);

    Optional<Project> findById(Long memberId);

}
