package com.creavispace.project.domain.jwt.dto;


import com.creavispace.project.domain.member.entity.Member;
import java.io.Serializable;
import lombok.Getter;

@Getter
public class JsonToken implements Serializable {
    private String token;
    private String message;

    public JsonToken(Object token) {
        this.token = validateTokenString(token);
        this.message = message(token);
    }

    public String message(Object token) {
        if (token == null) {
            return "로그인 되지 않은 사용자";
        }
        return "로그인 성공";
    }

    public String validateTokenString(Object token) {
        if (token == null) {
            return "";
        }
        return token.toString();
    }
}