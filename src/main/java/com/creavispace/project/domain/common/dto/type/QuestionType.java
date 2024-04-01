package com.creavispace.project.domain.common.dto.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum QuestionType {
    SUBJECTIVE("subjective","주관식"),
    OBJECTIVE("objective","객관식"),
    CHECKBOX("checkbox","체크박스");

    private String name;
    private String subName;
}
