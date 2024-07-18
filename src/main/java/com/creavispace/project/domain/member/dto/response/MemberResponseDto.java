package com.creavispace.project.domain.member.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class MemberResponseDto {

    private String memberId;
    private String profileUrl;
    private String nickname;
    private String email;
    private int career;
    private String position;
    private List<String> interestedStacks;
}
