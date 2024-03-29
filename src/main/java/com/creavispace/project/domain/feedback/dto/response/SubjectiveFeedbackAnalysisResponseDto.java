package com.creavispace.project.domain.feedback.dto.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class SubjectiveFeedbackAnalysisResponseDto extends FeedbackAnalysisResponseDto {
    private List<String> answers;
}