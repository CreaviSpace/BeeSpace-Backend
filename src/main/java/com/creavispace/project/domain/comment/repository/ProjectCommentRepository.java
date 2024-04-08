package com.creavispace.project.domain.comment.repository;

import com.creavispace.project.domain.comment.entity.ProjectComment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectCommentRepository extends JpaRepository<ProjectComment, Long> {
    public List<ProjectComment> findByProjectId(Long projectId);

    List<ProjectComment> findByMemberId(String memberId);

    List<ProjectComment> findByMemberId(String memberId, Pageable pageRequest);
}
