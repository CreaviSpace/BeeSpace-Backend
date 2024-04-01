package com.creavispace.project.domain.common.dto.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ProjectCategory {
    TEAM("team","팀"),
    INDIVIDUAL("individual","개인");

    private String name;
    private String subName;
}
