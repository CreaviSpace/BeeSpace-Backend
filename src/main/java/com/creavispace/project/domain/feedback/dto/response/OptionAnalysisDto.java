package com.creavispace.project.domain.feedback.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OptionAnalysisDto {
    private Long optionId;
    private String optionText;
    private int selectedCount;

}
