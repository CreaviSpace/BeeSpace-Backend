package com.creavispace.project.domain.bookmark.repository;

import com.creavispace.project.domain.bookmark.entity.ProjectBookmark;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectBookmarkRepository extends JpaRepository<ProjectBookmark, Long>{
    public Boolean existsByProjectIdAndMemberId(Long projectId, String memberId);
    public ProjectBookmark findByProjectIdAndMemberId(Long projectId, String memberId);


    //ky
    List<ProjectBookmark> findByMemberId(String memberId, Pageable pageable);

    List<ProjectBookmark> findByMemberIdOrderByContentsCreatedDateAsc(Long memberId, Pageable pageRequest);
    List<ProjectBookmark> findByMemberIdOrderByContentsCreatedDateDesc(Long memberId, Pageable pageRequest);


    List<ProjectBookmark> findByProjectIdIn(List<Long> projectIds);
}
