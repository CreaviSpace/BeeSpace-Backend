package com.creavispace.project.domain.feedback.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.creavispace.project.domain.feedback.entity.FeedbackQuestion;

@Repository
public interface FeedbackQuestionRepository extends JpaRepository<FeedbackQuestion, Long> {

    @Modifying
    @Query(value = "DELETE FROM feedback_question WHERE id NOT IN (:ids)", nativeQuery = true)
    public void deleteByQuestionIds(@Param("ids") List<Long> modifyFeedbackQuestionIds);

    public List<FeedbackQuestion> findByProjectId(Long projectId);
}
