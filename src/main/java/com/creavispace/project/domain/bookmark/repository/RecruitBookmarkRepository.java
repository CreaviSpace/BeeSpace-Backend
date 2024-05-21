package com.creavispace.project.domain.bookmark.repository;

import com.creavispace.project.domain.bookmark.entity.RecruitBookmark;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecruitBookmarkRepository extends JpaRepository<RecruitBookmark, Long> {
    public RecruitBookmark findByRecruitIdAndMemberId(Long recruitId, String memberId);

    List<RecruitBookmark> findByMemberId(String memberId, Pageable pageable);

    List<RecruitBookmark> findByMemberIdOrderByContentsCreatedDateDesc(String memberId, Pageable pageRequest);

    List<RecruitBookmark> findByMemberIdOrderByContentsCreatedDateAsc(String memberId, Pageable pageRequest);

    List<RecruitBookmark> findByMemberIdAndEnableTrue(String memberId, Pageable pageRequest);
}
