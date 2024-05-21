package com.creavispace.project.common.dto.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RecruitCategory {
    PROJECT_RECRUIT("프로젝트 모집"),
    STUDY("스터디");

    private final String subName;
}
