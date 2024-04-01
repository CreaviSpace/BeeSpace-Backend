package com.creavispace.project.domain.common.dto.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CommunityCategory {
    QNA("QnA"), 
    CHAT("수다"), 
    CONCERN("고민");

    private String subName;
}
