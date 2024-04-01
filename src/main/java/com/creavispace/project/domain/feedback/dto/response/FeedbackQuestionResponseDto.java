package com.creavispace.project.domain.feedback.dto.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackQuestionResponseDto {
    private Long questionId;
    private String question;
    private String questionType;
    private List<ChoiceItemResponseDto> choiceItems;
}
