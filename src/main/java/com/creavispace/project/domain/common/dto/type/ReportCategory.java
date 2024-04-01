package com.creavispace.project.domain.common.dto.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ReportCategory {
    SPAM("spam","스팸"),
    PORNOGRAPHY("pornography","음란물"),
    VIOLENCE("violence","폭력"),
    HATE_SPEECH("hate-speech","혐오발언"),
    PERSONAL_INFORMATION_DISCLOSURE("personal-information-disclosure","개인정보유출"),
    INAPPROPRIATE_CONTENT("inappropriate-content","부적절한 콘텐츠");

    private String name;
    private String subName;
}
