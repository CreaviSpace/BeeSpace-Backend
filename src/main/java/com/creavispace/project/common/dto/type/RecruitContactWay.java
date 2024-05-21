package com.creavispace.project.common.dto.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RecruitContactWay {
    OPENTALK("오픈톡"),
    EMAIL("이메일"),
    GOOGLE_FORM("구글폼");

    private final String subName;
}
