package com.creavispace.project.common.utils;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

public class UsableConst {
    private UsableConst usableConst;

    public static final String MEMBER_ID = "member-id";
    public static final String SORT_TYPE = "sort-type";

    public static final String AUTH_SUCCESS = "로그인 성공";
    public static final String AUTH_FAIL = "로그인 실패";

    public static String typeIsName(Object o) {
        String[] type = o.getClass().getName().split("\\.");
        return type[type.length - 1].toLowerCase();
    }



    public static PageRequest getPageRequest(Integer size, Integer page, String sortType) {
        if (sortType.equalsIgnoreCase("asc")) {
            return PageRequest.of(page - 1, size, Sort.by("createdDate").ascending());
        }
        return PageRequest.of(page - 1, size, Sort.by("createdDate").descending());
    }
}
