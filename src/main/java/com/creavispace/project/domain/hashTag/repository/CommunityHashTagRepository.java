package com.creavispace.project.domain.hashTag.repository;

import com.creavispace.project.domain.hashTag.entity.CommunityHashTag;
import com.creavispace.project.domain.hashTag.entity.CommunityHashTagResult;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommunityHashTagRepository extends JpaRepository<CommunityHashTag, Long>{
    public List<CommunityHashTag> findByCommunityId(Long communityId);

    @Transactional
    @Modifying
    @Query(value = "delete from community_hash_tag ch where ch.community_id = :communityId", nativeQuery = true)
    public void deleteByCommunityId(Long communityId);
    
    @Query(value = "SELECT ch.hash_tag_id hashTag, COUNT(*) count " +
    "FROM community_hash_tag ch " +
    "JOIN hash_tag h " +
    "ON ch.hash_tag_id = h.id " +
    "GROUP BY ch.hash_tag_id " +
    "ORDER BY count DESC " +
    "LIMIT 10", nativeQuery = true)
    public List<CommunityHashTagResult> findTop10HashTag();

    List<CommunityHashTag> findByCommunityIdIn(List<Long> communityIds);

    @EntityGraph(attributePaths = {"hashTag"}, type = EntityGraph.EntityGraphType.FETCH)
    @Query("select ch from CommunityHashTag ch join fetch ch.hashTag")
    List<CommunityHashTag> findByCommunityIdFetch(Long id);
}
