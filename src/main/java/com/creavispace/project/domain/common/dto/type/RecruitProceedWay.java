package com.creavispace.project.domain.common.dto.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RecruitProceedWay {
    ONLINE("online","온라인"),
    OFFLINE("offline","오프라인"),
    ON_OFFLINE("on-offline","온/오프라인");

    private String name;
    private String subName;
}
