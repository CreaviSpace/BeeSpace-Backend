package com.creavispace.project.domain.feedback.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.creavispace.project.domain.feedback.entity.FeedbackAnswer;

@Repository
public interface FeedbackAnswerRepository extends JpaRepository<FeedbackAnswer,Long> {
    public boolean existsByFeedbackQuestionIdAndMemberId(Long feedbackQuestionId, Long memberId);
    public List<FeedbackAnswer> findByFeedbackQuestionId(Long feedbackQeustionId);
}
