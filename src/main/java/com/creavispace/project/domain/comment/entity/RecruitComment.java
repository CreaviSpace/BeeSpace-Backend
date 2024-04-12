package com.creavispace.project.domain.comment.entity;

import com.creavispace.project.domain.comment.dto.request.CommentRequestDto;
import com.creavispace.project.domain.common.entity.BaseTimeEntity;
import com.creavispace.project.domain.member.entity.Member;
import com.creavispace.project.domain.recruit.entity.Recruit;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class RecruitComment extends BaseTimeEntity implements Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;

    @ManyToOne(targetEntity = Recruit.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "recruit_id", nullable = false)
    private Recruit recruit;

    @ManyToOne(targetEntity = Member.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    public void modify(CommentRequestDto dto){
        this.content = dto.getContent();
    }
}
