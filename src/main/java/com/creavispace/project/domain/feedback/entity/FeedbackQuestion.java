package com.creavispace.project.domain.feedback.entity;

import com.creavispace.project.common.dto.type.QuestionType;
import com.creavispace.project.common.entity.BaseTimeEntity;
import com.creavispace.project.domain.project.entity.Project;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackQuestion extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Column(nullable = false)
    private String questionText;
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private QuestionType questionType;

    @OneToMany(mappedBy = "feedbackQuestion", cascade = CascadeType.ALL)
    private List<QuestionOption> options;

    @OneToMany(mappedBy = "feedbackQuestion", cascade = CascadeType.ALL)
    private List<FeedbackAnswer> answers;

    public void setProject(Project project){
        this.project = project;
    }

    public void addOption(QuestionOption option){
        options.add(option);
        option.setFeedbackQuestion(this);
    }

    public void addAnswer(FeedbackAnswer answer){
        answers.add(answer);
        answer.setFeedbackQuestion(this);
    }

}
