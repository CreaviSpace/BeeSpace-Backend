package com.creavispace.project.domain.common.dto.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OrderBy {
    LATEST_ACTIVITY("latest-activity","최신활동순"),
    RECOMMENDED("recommended","추천순"),
    MOST_VIEWED("most-viewed","조회수순");

    private String name;
    private String subName;
}
