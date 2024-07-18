package com.creavispace.project.domain.recruit.entity;

import com.creavispace.project.common.dto.type.PostType;
import com.creavispace.project.common.post.entity.Post;
import com.creavispace.project.common.utils.TimeUtil;
import com.creavispace.project.domain.file.entity.RecruitImage;
import com.creavispace.project.domain.member.entity.Member;
import com.creavispace.project.domain.recruit.dto.request.RecruitRequestDto;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@JsonSerialize
public class Recruit extends Post {

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Category category;

    private int amount;

    @Enumerated(EnumType.STRING)
    private ProceedWay proceedWay;

    private int workDay;

    private LocalDate end;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ContactWay contactWay;

    @Column(nullable = false)
    private String contact;

    private RecruitmentStatus recruitmentStatus;

    @Builder.Default
    @OneToMany(mappedBy = "recruit", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecruitImage> recruitImages = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "recruit", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecruitTechStack> recruitTechStacks = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "recruit", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecruitPosition> recruitPositions = new ArrayList<>();

    public enum Category {PROJECT_RECRUIT, STUDY;}
    public enum ProceedWay {ONLINE, OFFLINE, ON_OFFLINE;}
    public enum ContactWay {OPENTALK, EMAIL, GOOGLE_FORM;}
    public enum RecruitmentStatus {RECRUITING, COMPLETED}
    //== 연관관계 메서드 ==//

    public void addRecruitImage(RecruitImage recruitImage){
        recruitImages.add(recruitImage);
        recruitImage.setRecruit(this);
    }
    public void addRecruitTechStack(RecruitTechStack recruitTechStack){
        recruitTechStacks.add(recruitTechStack);
        recruitTechStack.setRecruit(this);
    }

    public void addRecruitPosition(RecruitPosition recruitPosition){
        recruitPositions.add(recruitPosition);
        recruitPosition.setRecruit(this);
    }

    public static Recruit createRecruit(RecruitRequestDto dto, Member member, List<RecruitImage> recruitImages, List<RecruitTechStack> recruitTechStacks, List<RecruitPosition> recruitPositions) {
        // 모집 생성
        Recruit recruit = Recruit.builder()
                .category(dto.getCategory())
                .amount(dto.getAmount())
                .proceedWay(dto.getProceedWay())
                .workDay(dto.getWorkDay())
                .end(TimeUtil.getRecruitEnd(dto.getEnd(), dto.getEndFormat()))
                .contactWay(dto.getContactWay())
                .contact(dto.getContact())
                .build();
        // 게시글 공통정보 추가
        recruit.setup(PostType.RECRUIT, member, dto.getTitle(), dto.getContent(), null, null);
        // 모집 이미지 추가
        for(RecruitImage recruitImage : recruitImages){
            recruit.addRecruitImage(recruitImage);
        }
        // 모집 포지션 추가
        for(RecruitPosition recruitPosition : recruitPositions){
            recruit.addRecruitPosition(recruitPosition);
        }
        // 모집 기술스택 추가
        for(RecruitTechStack recruitTechStack : recruitTechStacks){
            recruit.addRecruitTechStack(recruitTechStack);
        }
        return recruit;
    }

    public void update(RecruitRequestDto dto) {
        this.changeTitleAndContentAndThumbnailAndBannerContent(dto.getTitle(), dto.getContent(),null,null);
        this.category = dto.getCategory();
        this.amount = dto.getAmount();
        this.workDay = dto.getWorkDay();
        this.contact = dto.getContact();
        this.contactWay = dto.getContactWay();
        this.proceedWay = dto.getProceedWay();
        this.end = TimeUtil.getRecruitEnd(dto.getEnd(), dto.getEndFormat());
    }

}
