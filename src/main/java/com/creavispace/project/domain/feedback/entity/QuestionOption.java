package com.creavispace.project.domain.feedback.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class QuestionOption {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "question_option_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "feedback_question_id", nullable = false)
    private FeedbackQuestion feedbackQuestion;

    @Column(nullable = false)
    private String optionText;

    public void setFeedbackQuestion(FeedbackQuestion feedbackQuestion){
        this.feedbackQuestion = feedbackQuestion;
    }

}
