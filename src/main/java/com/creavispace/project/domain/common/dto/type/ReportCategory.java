package com.creavispace.project.domain.common.dto.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ReportCategory {
    SPAM("스팸"),
    PORNOGRAPHY("음란물"),
    VIOLENCE("폭력"),
    HATE_SPEECH("혐오발언"),
    PERSONAL_INFORMATION_DISCLOSURE("개인정보유출"),
    INAPPROPRIATE_CONTENT("부적절한 콘텐츠");

    private String subName;
}
