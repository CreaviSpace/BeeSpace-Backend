package com.creavispace.project.config.auth.dto;

import lombok.Getter;

@Getter
public class RefreshJwtDto {
    String jwt;
    String memberId;

    public RefreshJwtDto(String jwt, String memberId) {
        this.jwt = jwt;
        this.memberId = memberId;
    }
}
