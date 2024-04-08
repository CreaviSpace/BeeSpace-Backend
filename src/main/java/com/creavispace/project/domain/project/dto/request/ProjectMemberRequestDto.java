package com.creavispace.project.domain.project.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectMemberRequestDto {
    private String memberId;
    // enum으로 관리 변경
    private String position;
}
