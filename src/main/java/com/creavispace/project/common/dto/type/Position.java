package com.creavispace.project.common.dto.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Position {
    PROJECT_MANAGER("프로젝트 매니저"),
    PRODUCT_MANAGER("프로덕트 매니저"),
    BUSINESS_ANALYST("비즈니스 애널리스트"),
    DESIGNER("디자이너"),
    FRONTEND_DEVELOPER("프론트엔드 개발자"),
    BACKEND_DEVELOPER("백엔드 개발자"),
    DATABASE_DEVELOPER("데이터베이스 개발자"),
    QA_ENGINEER("QA 엔지니어"),
    DEVOPS_ENGINEER("운영 엔지니어"),
    PLANNER("기획자");

    private final String subName;
}
