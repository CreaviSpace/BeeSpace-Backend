package com.creavispace.project.domain.feedback.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChoiceItemsAnalysisResponseDto{
    private String choiceItem;
    private int selectedCount;
}