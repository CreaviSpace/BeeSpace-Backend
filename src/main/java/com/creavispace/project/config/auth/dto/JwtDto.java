package com.creavispace.project.config.auth.dto;

import lombok.Getter;

@Getter
public class JwtDto {
    String jwt;
    String memberIdTag;
    boolean oldUser;

    public JwtDto(String jwt, String memberIdTag, boolean memberEnabled) {
        this.jwt = jwt;
        this.memberIdTag = memberIdTag;
        this.oldUser = memberEnabled;
    }


}
