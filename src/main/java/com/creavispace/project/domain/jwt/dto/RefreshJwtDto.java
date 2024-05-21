package com.creavispace.project.domain.jwt.dto;

import lombok.Getter;

@Getter
public class RefreshJwtDto {
    String jwt;
    String memberId;
    boolean success;
    public RefreshJwtDto(String jwt, String memberId) {
        this.jwt = jwt;
        this.memberId = memberId;
        success = false;
    }
}
