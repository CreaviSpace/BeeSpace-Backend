package com.creavispace.project.domain.bookmark.repository;

import com.creavispace.project.domain.bookmark.entity.CommunityBookmark;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommunityBookmarkRepository extends JpaRepository<CommunityBookmark, Long>{
    public CommunityBookmark findByCommunityIdAndMemberId(Long communityId, String memberId);

    List<CommunityBookmark> findByMemberId(String memberId, Pageable pageable);

    List<CommunityBookmark> findByMemberIdOrderByContentsCreatedDateAsc(String memberId, Pageable pageRequest);

    List<CommunityBookmark> findByMemberIdOrderByContentsCreatedDateDesc(String memberId, Pageable pageRequest);

}
