package com.creavispace.project.domain.bookmark.repository;

import com.creavispace.project.domain.bookmark.entity.RecruitBookmark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RecruitBookmarkRepository extends JpaRepository<RecruitBookmark, Long> {
    public Optional<RecruitBookmark> findByRecruitIdAndMemberId(Long recruitId, String memberId);
    public boolean existsByRecruitIdAndMemberId(Long recruitId, String memberId);

}
