package com.creavispace.project.domain.common.dto.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PostType {
    PROJECT("project","프로젝트"),
    COMMUNITY("community","커뮤니티"),
    RECRUIT("recruit","모집");

    private String name;
    private String subName;

}
