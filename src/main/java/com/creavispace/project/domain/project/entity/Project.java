package com.creavispace.project.domain.project.entity;

import com.creavispace.project.common.dto.type.PostType;
import com.creavispace.project.common.post.entity.Post;
import com.creavispace.project.domain.feedback.entity.FeedbackQuestion;
import com.creavispace.project.domain.file.entity.ProjectImage;
import com.creavispace.project.domain.member.entity.Member;
import com.creavispace.project.domain.project.dto.request.ProjectRequestDto;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@ToString(exclude = {"links", "projectMembers", "projectTechStacks", "projectImages", "feedbackQuestions"})
public class Project extends Post {

    private String field;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Category category;

    private int weekViewCount;

    @Builder.Default
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Link> links = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProjectMember> projectMembers = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProjectTechStack> projectTechStacks = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProjectImage> projectImages = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
    private List<FeedbackQuestion> feedbackQuestions = new ArrayList<>();

    public enum Category {TEAM, INDIVIDUAL}

    //== 연관관계 메서드 ==//

    public void addProjectLink(Link link){
        links.add(link);
        link.setProject(this);
    }
    public void addProjectMember(ProjectMember projectMember){
        projectMembers.add(projectMember);
        projectMember.setProject(this);
    }
    public void addProjectTechStack(ProjectTechStack projectTechStack){
        projectTechStacks.add(projectTechStack);
        projectTechStack.setProject(this);
    }

    public void addProjectImage(ProjectImage projectImage){
        projectImages.add(projectImage);
        projectImage.setProject(this);
    }

    public void addFeedbackQuestion(FeedbackQuestion feedbackQuestion){
        feedbackQuestions.add(feedbackQuestion);
        feedbackQuestion.setProject(this);
    }

    //== 생성 메서드 ==//

    public static Project createProject(ProjectRequestDto dto, Member member, List<ProjectMember> projectMembers, List<Link> links, List<ProjectTechStack> projectTechStacks, List<ProjectImage> projectImages){
        // 프로젝트 생성
        Project project = Project.builder()
                .field(dto.getField())
                .category(dto.getCategory())
                .weekViewCount(0)
                .build();
        // 프로젝트 공통정보 추가
        project.setup(PostType.PROJECT, member, dto.getTitle(), dto.getContent(), dto.getThumbnail(), dto.getContent());
        // 프로젝트 맴버 추가
        for(ProjectMember projectMember : projectMembers){
            project.addProjectMember(projectMember);
        }
        // 프로젝트 링크 추가
        for(Link link : links){
            project.addProjectLink(link);
        }
        // 프로젝트 기술스택 추가
        for(ProjectTechStack projectTechStack : projectTechStacks){
            project.addProjectTechStack(projectTechStack);
        }
        // 프로젝트 이미지 추가
        for(ProjectImage projectImage : projectImages){
            project.addProjectImage(projectImage);
        }
        return project;
    }
    //== 수정 메서드 ==//

    public void update(ProjectRequestDto dto) {
        this.changeTitleAndContentAndThumbnailAndBannerContent(dto.getTitle(), dto.getContent(), dto.getThumbnail(), dto.getBannerContent());
        this.field = dto.getField();
        this.category = dto.getCategory();
    }

}
