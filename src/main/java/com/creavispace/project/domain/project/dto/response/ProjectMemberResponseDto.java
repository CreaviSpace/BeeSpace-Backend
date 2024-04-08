package com.creavispace.project.domain.project.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectMemberResponseDto {
    private String memberId;
    private String memberProfile;
    private String memberNickname;
}
