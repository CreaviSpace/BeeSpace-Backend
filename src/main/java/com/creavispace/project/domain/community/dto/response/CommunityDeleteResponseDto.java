package com.creavispace.project.domain.community.dto.response;

import com.creavispace.project.global.dto.DeleteResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommunityDeleteResponseDto extends DeleteResponseDto {
    private Long communityId;
    private String postType;
}
