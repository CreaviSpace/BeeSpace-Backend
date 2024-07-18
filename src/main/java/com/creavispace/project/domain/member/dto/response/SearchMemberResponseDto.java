package com.creavispace.project.domain.member.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SearchMemberResponseDto {
    private String nickname;
    private String memberId;
}
