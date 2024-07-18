package com.creavispace.project.domain.feedback.dto.request;

import com.creavispace.project.common.dto.type.QuestionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackQuestionCreateRequestDto {
    private String question;
    private QuestionType questionType;
    private List<String> choiceItems;
    
}
