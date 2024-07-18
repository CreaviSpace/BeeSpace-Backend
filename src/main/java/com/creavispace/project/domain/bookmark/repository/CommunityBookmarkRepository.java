package com.creavispace.project.domain.bookmark.repository;

import com.creavispace.project.domain.bookmark.entity.CommunityBookmark;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommunityBookmarkRepository extends JpaRepository<CommunityBookmark, Long>{
    Optional<CommunityBookmark> findByCommunityIdAndMemberId(Long communityId, String memberId);
    public boolean existsByCommunityIdAndMemberId(Long communityId, String memberId);

}
