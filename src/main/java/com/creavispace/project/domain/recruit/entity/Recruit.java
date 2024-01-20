package com.creavispace.project.domain.recruit.entity;

import com.creavispace.project.domain.bookmark.entity.RecruitBookmark;
import com.creavispace.project.domain.comment.entity.RecruitComment;
import com.creavispace.project.domain.common.entity.BaseTimeEntity;
import com.creavispace.project.domain.tech.entity.RecruitTechStack;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Recruit extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long memberId;

    @Column(nullable = false)
    private String kind;

    private int amount;

    private String proceedWay;

    private LocalDateTime startDay;

    private LocalDateTime endDay;

    private int workDay;

    private LocalDateTime end;

    @Column(nullable = false)
    private String contact;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    private String thumbnail;

    @Column(nullable = false)
    private boolean status;

    private int viewCount;

    private String contactWay;

    @OneToMany(mappedBy = "recruit")
    private List<RecruitPosition> positionList;

    @OneToMany(mappedBy = "recruit")
    private List<RecruitTechStack> techStackList;

    @OneToMany(mappedBy = "recruit")
    private List<RecruitComment> commentList;

    @OneToMany(mappedBy = "recruit")
    private List<RecruitBookmark> bookmarkList;
}
