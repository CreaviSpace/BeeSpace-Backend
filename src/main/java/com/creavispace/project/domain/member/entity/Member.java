package com.creavispace.project.domain.member.entity;

import com.creavispace.project.common.entity.BaseTimeEntity;
import com.creavispace.project.common.post.entity.Post;
import com.creavispace.project.domain.alarm.entity.Alarm;
import com.creavispace.project.domain.auth.jwt.entity.RefreshToken;
import com.creavispace.project.domain.bookmark.entity.CommunityBookmark;
import com.creavispace.project.domain.bookmark.entity.ProjectBookmark;
import com.creavispace.project.domain.bookmark.entity.RecruitBookmark;
import com.creavispace.project.domain.like.entity.CommunityLike;
import com.creavispace.project.domain.like.entity.ProjectLike;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

//이메일
//비밀번호
//사용자 이름
//사용자 닉네임
//생성일
//수정일
//계정 탈퇴
//계정 활성화
//권한
//이메일 인증
//자기소개
@Entity
@Builder
@ToString
@Getter
@Setter
@Table(name = "member")
@NoArgsConstructor
@AllArgsConstructor
public class Member extends BaseTimeEntity {
    @Id
    @Column(name = "member_id")
    private String id;

    private String provider;

    private String memberEmail;

    private String memberNickname;

    private String profileUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    private String memberIntroduce;

    private String memberPosition;

    private Integer memberCareer;

    @OneToOne(mappedBy = "member", cascade = CascadeType.REMOVE)
    private RefreshToken refreshToken;

    @Builder.Default
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InterestTechStack> interestedStack = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "member", cascade = CascadeType.REMOVE)
    private List<Post> posts = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "member", cascade = CascadeType.REMOVE)
    private List<CommunityBookmark> communityBookmarks = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "member", cascade = CascadeType.REMOVE)
    private List<ProjectBookmark> projectBookmarks = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "member", cascade = CascadeType.REMOVE)
    private List<RecruitBookmark> recruitBookmarks = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "member", cascade = CascadeType.REMOVE)
    private List<CommunityLike> communityLikes = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "member", cascade = CascadeType.REMOVE)
    private List<ProjectLike> projectLikes = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "member", cascade = CascadeType.REMOVE)
    private List<Alarm> alarms = new ArrayList<>();

    public void changeRole(Role role){
        this.role = role;
    }

}
