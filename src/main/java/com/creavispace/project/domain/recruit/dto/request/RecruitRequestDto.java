package com.creavispace.project.domain.recruit.dto.request;

import com.creavispace.project.domain.recruit.entity.Recruit;
import lombok.*;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class RecruitRequestDto {
    private Recruit.Category category;
    private int amount;
    private int workDay;
    private String contact;
    private Recruit.ContactWay contactWay;
    private Recruit.ProceedWay proceedWay;
    private String end;
    private String endFormat;
    private String title;
    private String content;
    private List<RecruitPositionRequestDto> positions;
    private List<RecruitTechStackRequestDto> techStacks;
    private List<String> images;
}
