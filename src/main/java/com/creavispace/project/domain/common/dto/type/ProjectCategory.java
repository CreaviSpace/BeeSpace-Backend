package com.creavispace.project.domain.common.dto.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ProjectCategory {
    TEAM("팀"),
    INDIVIDUAL("개인");

    private String subName;
}
