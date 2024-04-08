package com.creavispace.project.domain.comment.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.creavispace.project.domain.comment.entity.RecruitComment;

@Repository
public interface RecruitCommentRepository extends JpaRepository<RecruitComment, Long> {
    public List<RecruitComment> findByRecruitId(Long recruitId);

    List<RecruitComment> findByRecruitId(Long memberId, Pageable pageRequest);

    List<RecruitComment> findByMemberId(Long memberId, Pageable pageRequest);
}
