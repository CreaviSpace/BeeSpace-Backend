package com.creavispace.project.domain.common.dto.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RecruitCategory {
    PROJECT_RECRUIT("project-recruit","프로젝트 모집"),
    STUDY("study","스터디");

    private String name;
    private String subName;
}
