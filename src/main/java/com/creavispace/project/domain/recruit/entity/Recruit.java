package com.creavispace.project.domain.recruit.entity;

import com.creavispace.project.domain.bookmark.entity.RecruitBookmark;
import com.creavispace.project.domain.comment.entity.RecruitComment;
import com.creavispace.project.common.dto.type.RecruitCategory;
import com.creavispace.project.common.dto.type.RecruitContactWay;
import com.creavispace.project.common.dto.type.RecruitProceedWay;
import com.creavispace.project.common.entity.BaseTimeEntity;
import com.creavispace.project.common.Post;
import com.creavispace.project.domain.member.entity.Member;
import com.creavispace.project.domain.recruit.dto.request.RecruitRequestDto;
import com.creavispace.project.common.exception.GlobalErrorCode;
import com.creavispace.project.common.utils.CustomValueOf;
import com.creavispace.project.common.utils.TimeUtil;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@JsonSerialize
public class Recruit extends BaseTimeEntity implements Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(targetEntity = Member.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private RecruitCategory category;

    private int amount;

    @Enumerated(EnumType.STRING)
    private RecruitProceedWay proceedWay;

    private int workDay;

    private LocalDate end;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private RecruitContactWay contactWay;

    @Column(nullable = false)
    private String contact;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private Boolean status;

    private int viewCount;

    @OneToMany(mappedBy = "recruit")
    @JsonBackReference
    private List<RecruitPosition> positions;

    @OneToMany(mappedBy = "recruit")
    @JsonBackReference
    private List<RecruitTechStack> techStacks;

    @OneToMany(mappedBy = "recruit")
    @JsonBackReference
    private List<RecruitComment> comments;

    @OneToMany(mappedBy = "recruit")
    @JsonBackReference
    private List<RecruitBookmark> bookmarks;

    public void modify(RecruitRequestDto dto){
        this.category = CustomValueOf.valueOf(RecruitCategory.class, dto.getCategory(), GlobalErrorCode.NOT_FOUND_RECRUIT_CATEGORY);
        this.amount = dto.getAmount();
        this.workDay = dto.getWorkDay();
        this.contact = dto.getContact();
        this.contactWay = CustomValueOf.valueOf(RecruitContactWay.class, dto.getContactWay(), GlobalErrorCode.NOT_FOUND_RECRUIT_CONTACTWAY);
        this.proceedWay = CustomValueOf.valueOf(RecruitProceedWay.class, dto.getProceedWay(), GlobalErrorCode.NOT_FOUND_RECRUIT_PROCEEDWAY);
        this.end = TimeUtil.getRecruitEnd(dto.getEnd(), dto.getEndFormat());
        this.title = dto.getTitle();
        this.content = dto.getContent();
    }

    public boolean disable(){
        this.status = !status;
        return status;
    }

    public void plusViewCount(){
        this.viewCount++;
    }
}
