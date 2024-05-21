package com.creavispace.project.domain.project.dto.response;

import com.creavispace.project.common.DeleteResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectDeleteResponseDto extends DeleteResponseDto {
    private Long projectId;
    private String postType;
}
