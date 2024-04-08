package com.creavispace.project.config.auth.dto;

import lombok.Getter;

@Getter
public class JwtDto {
    String jwt;
    String memberId;
    boolean oldUser;

    public JwtDto(String jwt, String memberIdTag, boolean memberEnabled) {
        this.jwt = jwt;
        this.memberId = memberIdTag;
        this.oldUser = memberEnabled;
    }


}
