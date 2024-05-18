package com.creavispace.project.domain.project.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectRequestDto {
    private String category;
    private List<ProjectMemberRequestDto> memberDtos;
    private String title;
    private String content;
    private List<ProjectTechStackRequestDto> techStackDtos;
    private String field;
    private List<ProjectLinkRequestDto> linkDtos;
    private String thumbnail;
    private String bannerContent;
    private List<String> images;
}
