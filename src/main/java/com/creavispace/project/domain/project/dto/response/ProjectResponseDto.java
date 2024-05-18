package com.creavispace.project.domain.project.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectResponseDto {
    private Long id;
    private String postType;
    private String memberId;
    private String memberNickName;
    private String memberProfile;
    // enum 관리로 변경
    private String category;
    private String field;
    private String title;
    private String content;
    private String thumbnail;
    private String bannerContent;
    private Integer viewCount;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
    private List<ProjectPositionResponseDto> positions;
    private List<ProjectLinkResponseDto> links;
    private List<ProjectTechStackResponseDto> techStacks;
    private List<String> images;
}
