package com.creavispace.project.domain.feedback.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackAnalysisResponseDto {
    private String questionType;
    private String question;
}
