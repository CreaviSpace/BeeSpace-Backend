package com.creavispace.project.domain.community.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CommunityCategory {
    QNA("QnA"), 
    CHAT("수다"), 
    CONCERN("고민");

    private final String subName;

}
