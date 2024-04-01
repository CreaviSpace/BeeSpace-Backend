package com.creavispace.project.domain.common.dto.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Position {
    PROJECT_MANAGER("project-manager","프로젝트 매니저"),
    PRODUCT_MANAGER("product-manager","프로덕트 매니저"),
    BUSINESS_ANALYST("business-analyst","비즈니스 애널리스트"),
    DESIGNER("designer","디자이너"),
    FRONTEND_DEVELOPER("frontend-developer","프론트엔드 개발자"),
    BACKEND_DEVELOPER("backend-developer","백엔드 개발자"),
    DATABASE_DEVELOPER("database-developer","데이터베이스 개발자"),
    QA_ENGINEER("qa-engineer","QA 엔지니어"),
    DEVOPS_ENGINEER("devops-engineer", "운영 엔지니어"),
    PLANNER("planner","기획자");

    private String name;
    private String subName;
}
