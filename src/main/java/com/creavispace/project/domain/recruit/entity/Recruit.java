package com.creavispace.project.domain.recruit.entity;

import java.util.List;

import com.creavispace.project.domain.bookmark.entity.RecruitBookmark;
import com.creavispace.project.domain.comment.entity.RecruitComment;
import com.creavispace.project.domain.common.entity.BaseTimeEntity;
import com.creavispace.project.domain.member.entity.Member;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Recruit extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(targetEntity = Member.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false)
    private String category;

    private int amount;

    private String proceedWay;

    private int workDay;

    private LocalDateTime end;

    @Column(nullable = false)
    private String contactWay;

    @Column(nullable = false)
    private String contact;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private Boolean status;

    private int viewCount;

    @OneToMany(mappedBy = "recruit")
    private List<RecruitPosition> positions;

    @OneToMany(mappedBy = "recruit")
    private List<RecruitTechStack> techStacks;

    @OneToMany(mappedBy = "recruit")
    private List<RecruitComment> comments;

    @OneToMany(mappedBy = "recruit")
    private List<RecruitBookmark> bookmarks;
}
