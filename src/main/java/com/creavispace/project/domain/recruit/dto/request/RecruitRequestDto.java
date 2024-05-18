package com.creavispace.project.domain.recruit.dto.request;

import lombok.*;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class RecruitRequestDto {

    // enum 관리로 변경
    private String category;
    private int amount;
    private int workDay;
    private String contact;
    // enum 관리로 변경
    private String contactWay;
    // enum 관리로 변경
    private String proceedWay;
    private String end;
    private String endFormat;
    private String title;
    private String content;
    private List<RecruitPositionRequestDto> positions;
    private List<RecruitTechStackRequestDto> techStacks;
    private List<String> images;
}
