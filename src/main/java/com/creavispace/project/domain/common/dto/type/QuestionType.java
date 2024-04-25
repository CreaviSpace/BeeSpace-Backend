package com.creavispace.project.domain.common.dto.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum QuestionType {
    SUBJECTIVE("주관식"),
    OBJECTIVE("객관식"),
    CHECKBOX("체크박스");

    private final String subName;
}
