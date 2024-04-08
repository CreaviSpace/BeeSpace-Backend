package com.creavispace.project.domain.feedback.repository;

import com.creavispace.project.domain.feedback.entity.FeedbackAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeedbackAnswerRepository extends JpaRepository<FeedbackAnswer,Long> {
    public boolean existsByFeedbackQuestionIdAndMemberId(Long feedbackQuestionId, String memberId);
    public List<FeedbackAnswer> findByFeedbackQuestionId(Long feedbackQuestionId);
}
