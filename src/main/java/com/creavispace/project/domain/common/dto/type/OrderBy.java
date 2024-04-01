package com.creavispace.project.domain.common.dto.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OrderBy {
    LATEST_ACTIVITY("최신활동순"),
    RECOMMENDED("추천순"),
    MOST_VIEWED("조회수순");

    private String subName;
}
