package com.creavispace.project.domain.feedback.repository;

import com.creavispace.project.domain.feedback.entity.QuestionOption;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionOptionRepository extends JpaRepository<QuestionOption, Long> {
}
