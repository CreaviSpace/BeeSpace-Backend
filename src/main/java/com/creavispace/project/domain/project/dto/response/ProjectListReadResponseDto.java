package com.creavispace.project.domain.project.dto.response;

import java.util.List;

import com.creavispace.project.domain.search.dto.response.SearchListReadResponseDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ProjectListReadResponseDto implements SearchListReadResponseDto{
    private Long id;
    private String postType;
    private String category;
    private String title;
    private List<ProjectLinkResponseDto> links;
    private String thumbnail;
    private String bannerContent;
    private boolean status;
}
