package com.creavispace.project.domain.feedback.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.creavispace.project.domain.feedback.entity.ChoiceItem;

@Repository
public interface ChoiceItemRepository extends JpaRepository<ChoiceItem, Long> {
    public List<ChoiceItem> findByFeedbackQuestionId(Long feedbackQuestionId);
    public Optional<ChoiceItem> findByIdAndFeedbackQuestionId(Long id, Long feedbackQuestionId);
}
