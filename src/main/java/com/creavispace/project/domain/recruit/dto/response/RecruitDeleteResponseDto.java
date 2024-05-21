package com.creavispace.project.domain.recruit.dto.response;

import com.creavispace.project.common.DeleteResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecruitDeleteResponseDto extends DeleteResponseDto {
    private Long recruitId;
    private String postType;
}
