package com.creavispace.project.domain.project.entity;

import com.creavispace.project.common.entity.BaseTimeEntity;
import com.creavispace.project.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"member_id", "position"})})
public class ProjectMember extends BaseTimeEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    private String position;

    // 프로젝트 연관관계를 위한 메서드
    public void setProject(Project project) {
        this.project = project;
    }

    //== 생성 메서드 ==//
    public static ProjectMember createProjectMember(Member member, String position){
        return ProjectMember.builder()
                .member(member)
                .position(position)
                .build();
    }

}