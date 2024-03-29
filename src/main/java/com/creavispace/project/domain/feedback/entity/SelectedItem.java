package com.creavispace.project.domain.feedback.entity;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class SelectedItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "feedback_answer_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private FeedbackAnswer feedbackAnswer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "choice_item_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private ChoiceItem choiceItem;
}
