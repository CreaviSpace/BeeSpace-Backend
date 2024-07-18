package com.creavispace.project.domain.feedback.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackAnalysisResponseDto {
    private Long questionId;
    private String questionType;
    private String questionText;
    private List<OptionAnalysisDto> selectedOptions;
    private List<String> answers;
}
