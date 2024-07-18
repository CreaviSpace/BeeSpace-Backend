package com.creavispace.project.domain.recruit.dto.request;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class RecruitPositionRequestDto {
    private String position;
    private int amount;
    private int now;
    
}
