package com.creavispace.project.domain.member.dto.response;

import lombok.Getter;

@Getter
public class MemberInterestedStackResponseDto {
    private String techStack;

    public MemberInterestedStackResponseDto(String techStack) {
        this.techStack = techStack;
    }
}
