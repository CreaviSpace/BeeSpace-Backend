package com.creavispace.project.domain.project.dto.response;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ProjectListReadResponseDto{
    private Long id;
    private String postType;
    private String category;
    private String title;
    private List<ProjectLinkResponseDto> links;
    private String thumbnail;
    private String bannerContent;
    private String status;
    private LocalDateTime createdDate;
    private String memberNickname;
    private String memberProfile;
}
