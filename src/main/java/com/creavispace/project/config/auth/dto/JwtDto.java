package com.creavispace.project.config.auth.dto;

import com.creavispace.project.global.dto.Dto;
import lombok.Getter;

@Getter
public class JwtDto implements Dto {
    String jwt;
    String memberId;
    boolean fired;
    boolean oldUser;
    String message;

    public JwtDto(String jwt, String memberId, String message, boolean memberEnabled, boolean fired) {
        this.jwt = jwt;
        this.memberId = memberId;
        this.oldUser = memberEnabled;
        this.fired = fired;
        this.message = message;
    }


}
