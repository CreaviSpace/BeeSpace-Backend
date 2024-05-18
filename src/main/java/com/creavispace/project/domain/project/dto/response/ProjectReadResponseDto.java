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
public class ProjectReadResponseDto {
    private Long id;
    private String postType;
    private String memberId;
    private String memberNickName;
    private String memberProfile;
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
    private String projectViewLog;
    private List<String> images;

}
