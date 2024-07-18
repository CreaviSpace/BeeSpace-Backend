package com.creavispace.project.domain.member.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {
    WITHDRAWN("ROLE_WITHDRAWN", "탈퇴한 사용자"),
    INCOMPLETE_MEMBER("ROLE_INCOMPLETE_MEMBER", "추가 정보 입력이 필요한 사용자"),
    MEMBER("ROLE_MEMBER", "일반 사용자"),
    ADMIN("ROLE_ADMIN", "관리자 사용자");

    private final String key;
    private final String title;

    public static Role fromKey(String key){
        for(Role role : Role.values()){
            if(role.getKey().equals(key)){
                return role;
            }
        }
        throw new IllegalArgumentException("No enum constant with key " + key);
    }
}
