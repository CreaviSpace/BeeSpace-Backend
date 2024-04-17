package com.creavispace.project.domain.community.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.creavispace.project.domain.community.entity.CommunityHashTag;
import com.creavispace.project.domain.hashTag.entity.CommunityHashTagResult;

import jakarta.transaction.Transactional;

@Repository
public interface CommunityHashTagRepository extends JpaRepository<CommunityHashTag, Long>{
    public List<CommunityHashTag> findByCommunityId(Long communityId);

    @Transactional
    public void deleteByCommunityId(Long communityId);
    
    @Query(value = "SELECT ch.hash_tag_id hashTagId, h.hash_tag hashTag, COUNT(*) count " +
    "FROM community_hash_tag ch " +
    "JOIN hash_tag h " +
    "ON ch.hash_tag_id = h.id " +
    "GROUP BY ch.hash_tag_id " +
    "ORDER BY count DESC " +
    "LIMIT 10", nativeQuery = true)
    public List<CommunityHashTagResult> findTop10HashTag();

    List<CommunityHashTag> findByCommunityIdIn(List<Long> communityIds);
}
