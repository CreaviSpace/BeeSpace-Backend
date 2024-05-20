package com.creavispace.project.domain.project.entity;

import com.creavispace.project.domain.bookmark.entity.ProjectBookmark;
import com.creavispace.project.domain.comment.entity.ProjectComment;
import com.creavispace.project.domain.common.dto.type.ProjectCategory;
import com.creavispace.project.domain.common.entity.BaseTimeEntity;
import com.creavispace.project.domain.common.entity.Post;
import com.creavispace.project.domain.like.entity.ProjectLike;
import com.creavispace.project.domain.member.entity.Member;
import com.creavispace.project.domain.project.dto.request.ProjectRequestDto;
import com.creavispace.project.global.exception.GlobalErrorCode;
import com.creavispace.project.global.util.CustomValueOf;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Project extends BaseTimeEntity implements Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(targetEntity = Member.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    private String field;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ProjectCategory category;

    @Column(length = 200, nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private String thumbnail;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String bannerContent;

    private int likeCount;

    private int viewCount;

    private int weekViewCount;

    private boolean status;

    private boolean feedback;
    @JsonBackReference
    @OneToMany(mappedBy = "project", fetch = FetchType.LAZY)
    private List<ProjectLink> links;
    @JsonBackReference
    @OneToMany(mappedBy = "project", fetch = FetchType.LAZY)
    private List<ProjectComment> comments;
    @JsonBackReference
    @OneToMany(mappedBy = "project", fetch = FetchType.LAZY)
    private List<ProjectBookmark> bookmarks;
    @JsonBackReference
    @OneToMany(mappedBy = "project", fetch = FetchType.LAZY)
    private List<ProjectLike> likes;
    @JsonBackReference
    @OneToMany(mappedBy = "project", fetch = FetchType.LAZY)
    private List<ProjectMember> members;
    @JsonBackReference
    @OneToMany(mappedBy = "project", fetch = FetchType.LAZY)
    private List<ProjectTechStack> techStacks;

    public void modify(ProjectRequestDto dto){
        this.category = CustomValueOf.valueOf(ProjectCategory.class, dto.getCategory(), GlobalErrorCode.NOT_FOUND_PROJECT_CATEGORY);
        this.title = dto.getTitle();
        this.content = dto.getContent();
        this.field = dto.getField();
        this.thumbnail = dto.getThumbnail();
        this.bannerContent = dto.getBannerContent();
    }

    public boolean disable(){
        this.status = !status;
        return status;
    }

    public void plusViewCount(){
        this.viewCount++;
        this.weekViewCount++;
    }

    public void feedbackTrue(){
        this.feedback = true;
    }

    public void feedbackFalse(){
        this.feedback = false;
    }

    public void resetWeekViewCount(){
        this.weekViewCount = 0;
    }

    public void pulsLikeCount(){
        this.likeCount++;
    }

    public void minusLikeCount(){
        this.likeCount--;
    }

}
