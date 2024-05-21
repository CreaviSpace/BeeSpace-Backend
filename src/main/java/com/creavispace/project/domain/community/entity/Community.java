package com.creavispace.project.domain.community.entity;

import com.creavispace.project.common.dto.type.CommunityCategory;
import com.creavispace.project.common.entity.BaseTimeEntity;
import com.creavispace.project.common.Post;
import com.creavispace.project.domain.community.dto.request.CommunityRequestDto;
import com.creavispace.project.domain.member.entity.Member;
import com.creavispace.project.common.exception.GlobalErrorCode;
import com.creavispace.project.common.utils.CustomValueOf;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@JsonSerialize
@ToString
public class Community extends BaseTimeEntity implements Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(targetEntity = Member.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CommunityCategory category;
    
    @Column(nullable = false)
    private String title;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    private int viewCount;

    private int likeCount;

    private boolean status;

    public void modify(CommunityRequestDto dto){
        this.category = CustomValueOf.valueOf(CommunityCategory.class,dto.getCategory(),GlobalErrorCode.NOT_FOUND_COMMUNITY_CATEGORY);
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

    public void plusLikeCount() {
        this.likeCount++;
    }

    public void minusLikeCount() {
        this.likeCount--;
    }
}
