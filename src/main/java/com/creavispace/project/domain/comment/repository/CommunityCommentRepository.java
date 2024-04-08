package com.creavispace.project.domain.comment.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.creavispace.project.domain.comment.entity.CommunityComment;

@Repository
public interface CommunityCommentRepository extends JpaRepository<CommunityComment, Long>{
    public List<CommunityComment> findByCommunityId(Long communityId);

    List<CommunityComment> findByCommunityId(Long memberId, Pageable pageRequest);

    List<CommunityComment> findByMemberId(Long memberId, Pageable pageRequest);
}
