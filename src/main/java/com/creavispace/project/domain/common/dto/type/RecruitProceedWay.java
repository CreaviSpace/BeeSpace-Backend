package com.creavispace.project.domain.common.dto.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RecruitProceedWay {
    ONLINE("온라인"),
    OFFLINE("오프라인"),
    ON_OFFLINE("온/오프라인");

    private final String subName;
}
