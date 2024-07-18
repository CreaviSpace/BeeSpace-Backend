package com.creavispace.project.domain.member.entity;

import com.creavispace.project.domain.techStack.entity.TechStack;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"member_id", "tech_stack_id"}))
public class InterestTechStack {

    @Id
    @Column(name = "interest_tech_stack_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tech_stack_id")
    private TechStack techStack;
}
