package com.creavispace.project.common.post.entity;

import com.creavispace.project.common.dto.type.PostType;
import com.creavispace.project.common.entity.BaseTimeEntity;
import com.creavispace.project.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public abstract class Post extends BaseTimeEntity {

    @Id
    @GeneratedValue
    @Column(name = "post_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    private PostType postType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(length = 200, nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    private int viewCount;

    private String thumbnail;

    @Column(columnDefinition = "TEXT")
    private String bannerContent;

    @Enumerated(EnumType.STRING)
    private Status status;

    public enum Status {PUBLIC, PRIVATE, REPORT, DELETE}

    public void setMember(Member member){
        this.member = member;
        member.getPosts().add(this);
    }

    public void setup(PostType postType, Member member, String title, String content, String thumbnail, String bannerContent){
        this.postType = postType;
        this.member = member;
        this.title = title;
        this.content = content;
        this.viewCount = 0;
        this.thumbnail = thumbnail;
        this.bannerContent = bannerContent;
        this.status = Status.PUBLIC;
    }

    public void changeTitleAndContentAndThumbnailAndBannerContent(String title, String content, String thumbnail, String bannerContent){
        this.title = title;
        this.content = content;
        this.thumbnail = thumbnail;
        this.bannerContent = bannerContent;
    }

    public void changeStatus(Status status){
        this.status = status;
    }


}
