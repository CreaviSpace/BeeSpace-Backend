package com.creavispace.project.domain.bookmark.repository;

import com.creavispace.project.domain.bookmark.entity.ProjectBookmark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProjectBookmarkRepository extends JpaRepository<ProjectBookmark, Long>{
    public Boolean existsByProjectIdAndMemberId(Long projectId, String memberId);
    public Optional<ProjectBookmark> findByProjectIdAndMemberId(Long projectId, String memberId);
}
