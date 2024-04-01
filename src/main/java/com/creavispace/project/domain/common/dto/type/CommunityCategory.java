package com.creavispace.project.domain.common.dto.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CommunityCategory {
    QNA("qna","QnA"), 
    CHAT("chat","수다"), 
    CONCERN("concern", "고민");

    private String name;
    private String subName;
}
