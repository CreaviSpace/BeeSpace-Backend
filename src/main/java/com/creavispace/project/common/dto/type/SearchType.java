package com.creavispace.project.common.dto.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SearchType {

    PROJECT("프로젝트",PostType.PROJECT, null),
    COMMUNITY("커뮤니티",PostType.COMMUNITY,null),
    RECRUIT("모집",PostType.RECRUIT,null),
    TEAM("팀",PostType.PROJECT, "TEAM"),
    INDIVIDUAL("개인",PostType.PROJECT,"INDIVIDUAL"),
    QNA("QnA",PostType.COMMUNITY,"QNA"),
    CHAT("수다",PostType.COMMUNITY,"CHAT"),
    CONCERN("고민",PostType.COMMUNITY,"CONCERN"),
    PROJECT_RECRUIT("프로젝트 모집",PostType.RECRUIT,"PROJECT_RECRUIT"),
    STUDY("스터디",PostType.RECRUIT,"STUDY");

    private final String subName;
    private final PostType postType;
    private final String category;
}
