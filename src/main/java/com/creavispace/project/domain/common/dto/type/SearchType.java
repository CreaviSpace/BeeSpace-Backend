package com.creavispace.project.domain.common.dto.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SearchType {
    PROJECT("프로젝트"),
    COMMUNITY("커뮤니티"),
    RECRUIT("모집"),
    TEAM("팀"),
    INDIVIDUAL("개인"),
    QNA("QnA"), 
    CHAT("수다"), 
    CONCERN("고민"),
    PROJECT_RECRUIT("프로젝트 모집"),
    STUDY("스터디");

    private String subName;
}
