package com.creavispace.project.domain.common.dto.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OrderBy {
    LATEST_ACTIVITY("최신활동순", "created_date"),
    RECOMMENDED("추천순", "like_count"),
    MOST_VIEWED("조회수순", "view_count");

    private final String subName;
    private final String columnName;
}
