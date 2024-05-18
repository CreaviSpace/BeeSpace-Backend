package com.creavispace.project.domain.recruit.dto.response;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class RecruitResponseDto {
    private Long id;
    private String postType;
    private String memberId;
    private String memberNickName;
    private String memberProfile;
    private Integer viewCount;
    // enum으로 관리
    private String category;
    // enum으로 관리
    private String contactWay;
    private String contact;
    private Integer amount;
    // enum으로 관리
    private String proceedWay;
    private int workDay;
    private LocalDate end;
    private String title;
    private String content;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
    private List<RecruitPositionResponseDto> positions;
    private List<RecruitTechStackResponseDto> techStacks;
    private List<String> images;
}

