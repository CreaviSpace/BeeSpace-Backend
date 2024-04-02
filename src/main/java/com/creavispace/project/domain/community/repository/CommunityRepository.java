package com.creavispace.project.domain.community.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.creavispace.project.domain.community.entity.Community;
import com.creavispace.project.domain.search.entity.SearchResultSet;

@Repository
public interface CommunityRepository extends JpaRepository<Community, Long> {
    public boolean existsByIdAndMemberId(Long communityId, Long memberId);
    public Optional<Community> findByIdAndStatusTrue(Long communityId);
    
    @Query(value = "SELECT * " +
    "FROM community " +
    "WHERE id IN (" +
    "SELECT community_id " +
    "FROM community_hash_tag " +
    "WHERE hash_tag_id = :hashTagId) " +
    "AND category = :category " +
    "AND status = true " +
    "ORDER BY created_date DESC ", nativeQuery = true)
    public Page<Community> findAllByStatusTrueAndCategoryAndHashTagId(String category, Long hashTagId, Pageable pageable);

    @Query(value = "SELECT * " +
    "FROM community " +
    "WHERE id IN (" +
    "SELECT community_id " +
    "FROM community_hash_tag " +
    "WHERE hash_tag_id = :hashTagId) " +
    "AND status = true " +
    "ORDER BY created_date DESC ", nativeQuery = true)
    public Page<Community> findAllByStatusTrueAndHashTagId(Long hashTagId, Pageable pageable);

    public Page<Community> findAllByStatusTrueAndCategory(String category, Pageable pageable);

    public Page<Community> findAllByStatusTrue(Pageable pageable);

    @Query(value = "SELECT c.id, c.member_id, c.category, c.title, c.content, c.view_count, c.status, c.created_date, c.modified_date " +
    "FROM community c " +
    "LEFT JOIN ( " +
    "SELECT community_id, COUNT(*) AS likeCount " +
    "FROM community_like " +
    "GROUP BY community_id " +
    ") cl ON c.id = cl.community_id " +
    "WHERE c.id IN ( " +
    "SELECT ch.community_id " +
    "FROM community_hash_tag ch " +
    "WHERE ch.hash_tag_id = :hashTagId " +
    ") " +
    "AND c.category = :category " +
    "AND status = true " +
    "ORDER BY COALESCE(likeCount, 0) DESC ",nativeQuery = true)
    public Page<Community> findAllByStatusTrueAndCategoryAndHashTagIdOrderByLikeCountDesc(String category, Long hashTagId, Pageable pageable);

    @Query(value = "SELECT c.id, c.member_id, c.category, c.title, c.content, c.view_count, c.status, c.created_date, c.modified_date " +
    "FROM community c " +
    "LEFT JOIN ( " +
    "SELECT community_id, COUNT(*) AS likeCount " +
    "FROM community_like " +
    "GROUP BY community_id " +
    ") cl ON c.id = cl.community_id " +
    "WHERE c.id IN ( " +
    "SELECT ch.community_id " +
    "FROM community_hash_tag ch " +
    "WHERE ch.hash_tag_id = :hashTagId " +
    ") " +
    "AND status = true " +
    "ORDER BY COALESCE(likeCount, 0) DESC ",nativeQuery = true)
    public Page<Community> findAllByStatusTrueAndHashTagIdOrderByLikeCountDesc(Long hashTagId, Pageable pageable);

    @Query(value = "SELECT c.id, c.member_id, c.category, c.title, c.content, c.view_count, c.status, c.created_date, c.modified_date " +
    "FROM community c " +
    "LEFT JOIN ( " +
    "SELECT community_id, COUNT(*) AS likeCount " +
    "FROM community_like " +
    "GROUP BY community_id " +
    ") cl ON c.id = cl.community_id " +
    "WHERE c.category = :category " +
    "AND status = true " +
    "ORDER BY COALESCE(likeCount, 0) DESC ",nativeQuery = true)
    public Page<Community> findAllByStatusTrueAndCategoryOrderByLikeCountDesc(String category, Pageable pageable);

    @Query(value = "SELECT c.id, c.member_id, c.category, c.title, c.content, c.view_count, c.status, c.created_date, c.modified_date " +
    "FROM community c " +
    "LEFT JOIN ( " +
    "SELECT community_id, COUNT(*) AS likeCount " +
    "FROM community_like " +
    "GROUP BY community_id " +
    ") cl ON c.id = cl.community_id " +
    "WHERE status = true " +
    "ORDER BY COALESCE(cl.likeCount, 0) DESC ",nativeQuery = true)
    public Page<Community> findAllByStatusTrueOrderByLikeCountDesc(Pageable pageable);

    @Query(value = "SELECT * " +
    "FROM community " +
    "WHERE id IN (" +
    "SELECT community_id " +
    "FROM community_hash_tag " +
    "WHERE hash_tag_id = :hashTagId) " +
    "AND category = :category " +
    "AND status = true " +
    "ORDER BY view_count DESC ", nativeQuery = true)
    public Page<Community> findAllByStatusTrueAndCategoryAndHashTagIdOrderByViewCountDesc(String category, Long hashTagId, Pageable pageable);

    @Query(value = "SELECT * " +
    "FROM community " +
    "WHERE id IN (" +
    "SELECT community_id " +
    "FROM community_hash_tag " +
    "WHERE hash_tag_id = :hashTagId) " +
    "AND status = true " +
    "ORDER BY view_count DESC ", nativeQuery = true)
    public Page<Community> findAllByStatusTrueAndHashTagIdOrderByViewCountDesc(Long hashTagId, Pageable pageable);

    public Page<Community> findAllByStatusTrueAndCategoryOrderByViewCountDesc(String category, Pageable pageable);

    public Page<Community> findAllByStatusTrueOrderByViewCountDesc(Pageable pageable);

    @Query(value = "SELECT 'COMMUNITY' AS postType, c.id AS postId, c.created_date AS createdDate FROM community c WHERE (c.content LIKE %:text% OR c.title LIKE %:text%) AND c.status = true ORDER BY created_date DESC", nativeQuery = true)
    public Page<SearchResultSet> findCommunitySearchData(String text, Pageable pageable);


    //ky
    public Page<Community> findAllByMemberIdAndStatusTrueOrderByCreatedDateAsc(Long memberId, Pageable pageable);
    public Page<Community> findAllByMemberIdAndStatusTrueOrderByCreatedDateDesc(Long memberId, Pageable pageable);


    Optional<Community> findById(Long memberId);

    @Query(value = "SELECT 'COMMUNITY' AS postType, c.id AS postId, c.created_date AS createdDate FROM community c WHERE (c.content LIKE %:text% OR c.title LIKE %:text%) AND c.status = true AND c.category = :searchType ORDER BY created_date DESC", nativeQuery = true)
    public Page<SearchResultSet> findCommunityCategorySearchData(String text, String searchType, Pageable pageable);
}
