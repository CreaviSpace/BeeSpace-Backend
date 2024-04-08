package com.creavispace.project.domain.like.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.creavispace.project.domain.like.entity.CommunityLike;

@Repository
public interface CommunityLikeRepository extends JpaRepository<CommunityLike, Long> {
    public CommunityLike findByCommunityIdAndMemberId(Long communityId, String memberId);
    public boolean existsByCommunityIdAndMemberId(Long communityId, String memberId);
    public int countByCommunityId(Long communityId);
}
