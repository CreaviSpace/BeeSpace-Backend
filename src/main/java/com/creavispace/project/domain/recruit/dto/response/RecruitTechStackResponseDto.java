package com.creavispace.project.domain.recruit.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecruitTechStackResponseDto {
    private String techStack;
    private String iconUrl;
}
