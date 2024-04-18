package com.creavispace.project.domain.techStack.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TechStackListReadResponseDto {
    private String techStack;
    private String techStackIcon;
}
