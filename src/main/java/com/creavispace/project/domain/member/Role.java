package com.creavispace.project.domain.member;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {
    EX_MEMBER("ROLE_EX_MEMBER", "탈톼한 사용자"),
    MEMBER("ROLE_MEMBER", "일반 사용자"),
    ADMIN("ROLE_ADMIN", "관리자 사용자");

    private final String key;
    private final String title;
}
