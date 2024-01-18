package com.creavispace.project.domain.project.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import com.creavispace.project.domain.project.entity.Project;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectModifyResponseDto {
    private Long id;
    private String kind;
    private String field;
    private String title;
    private String content;
    private String thumbnail;
    private String bannerContent;
    private Integer viewCount;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
    private List<ProjectMemberResponseDto> memberList;
    private List<ProjectTechStackResponseDto> techStackList;
    private List<ProjectLinkResponseDto> linkList;

    public ProjectModifyResponseDto(Project project){
        this.id = project.getId();
        this.kind = project.getKind();
        this.field = project.getField();
        this.title = project.getTitle();
        this.content = project.getContent();
        this.thumbnail = project.getThumbnail();
        this.bannerContent = project.getBannerContent();
        this.viewCount = project.getViewCount();
        // this.createdDate = project.getCreatedDate();
        // this.modifiedDate = project.getModifiedDate();
        this.memberList = ProjectMemberResponseDto.copyList(project.getMemberList());
        this.techStackList = ProjectTechStackResponseDto.copyList(project.getTechStackList());
        this.linkList = ProjectLinkResponseDto.copyList(project.getLinkList());
    }
}
