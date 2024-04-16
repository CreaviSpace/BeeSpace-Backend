package com.creavispace.project.global.util;

public class UsableConst {
    private UsableConst usableConst;

    public static final String MEMBER_ID = "member-id";
    public static final String SORT_TYPE = "sort-type";

    public static String typeIsName(Object o) {
        String[] type = o.getClass().getName().split("\\.");
        return type[type.length - 1].toLowerCase();
    }


}
