package com.creavispace.project.domain.recruit.repository;

import com.creavispace.project.domain.common.dto.type.RecruitCategory;
import com.creavispace.project.domain.recruit.entity.Recruit;
import com.creavispace.project.domain.search.entity.SearchResultSet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RecruitRepository extends JpaRepository<Recruit, Long> {
    public Page<Recruit> findAllByStatusTrueAndCategory(RecruitCategory category, Pageable pageRequest);
    public Page<Recruit> findAllByStatusTrue(Pageable pageRequest);
    public Optional<Recruit> findByIdAndStatusTrue(Long recruitId);
    public boolean existsByIdAndMemberId(Long recruitId, String memberId);
    public List<Recruit> findTop3ByStatusTrueOrderByEndAsc();

    @Query(value = "SELECT 'RECRUIT' AS postType, r.id AS postId, r.created_date AS createdDate FROM recruit r WHERE (r.content LIKE %:text% OR r.title LIKE %:text%) AND r.status = true ORDER BY created_date DESC", nativeQuery = true)
    public Page<SearchResultSet> findRecruitSearchData(String text, Pageable pageable);


    //ky
    Page<Recruit> findByMemberIdAndStatusTrueOrderByCreatedDateAsc(String memberId, Pageable pageRequest);
    Page<Recruit> findByMemberIdAndStatusTrueOrderByCreatedDateDesc(String memberId, Pageable pageRequest);

//    Optional<Recruit> findById(String memberId);

    @Query(value = "SELECT 'RECRUIT' AS postType, r.id AS postId, r.created_date AS createdDate FROM recruit r WHERE (r.content LIKE %:text% OR r.title LIKE %:text%) AND r.status = true AND r.category = :searchType ORDER BY created_date DESC", nativeQuery = true)
    public Page<SearchResultSet> findRecruitCategorySearchData(String text, String searchType, Pageable pageable);

    Page<Recruit> findByStatusFalse(Pageable pageRequest);
}
