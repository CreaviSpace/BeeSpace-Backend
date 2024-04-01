package com.creavispace.project.domain.common.dto.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RecruitContactWay {
    OPENTALK("opentalk","오픈톡"),
    EMAIL("email","이메일"),
    GOOGLE_FORM("google-form","구글폼");

    private String name;
    private String subName;
}
