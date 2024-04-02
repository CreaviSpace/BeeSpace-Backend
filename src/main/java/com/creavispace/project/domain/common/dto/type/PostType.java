package com.creavispace.project.domain.common.dto.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PostType {
    PROJECT("프로젝트"),
    COMMUNITY("커뮤니티"),
    RECRUIT("모집");

    private String subName;
}
