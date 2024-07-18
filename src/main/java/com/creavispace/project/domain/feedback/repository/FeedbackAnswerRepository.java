package com.creavispace.project.domain.feedback.repository;

import com.creavispace.project.domain.feedback.entity.FeedbackAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FeedbackAnswerRepository extends JpaRepository<FeedbackAnswer,Long> {
    public int countBySelectedOptionId(Long optionId);
}
