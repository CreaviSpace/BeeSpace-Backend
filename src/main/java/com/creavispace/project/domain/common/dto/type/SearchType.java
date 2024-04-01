package com.creavispace.project.domain.common.dto.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SearchType {
    PROJECT("project","프로젝트"),
    COMMUNITY("community","커뮤니티"),
    RECRUIT("recruit","모집"),
    TEAM("team","팀"),
    INDIVIDUAL("individual","개인"),
    QNA("qna","QnA"), 
    CHAT("chat","수다"), 
    CONCERN("concern", "고민"),
    PROJECT_RECRUIT("project-recruit","프로젝트 모집"),
    STUDY("study","스터디");

    private String name;
    private String subName;
}
