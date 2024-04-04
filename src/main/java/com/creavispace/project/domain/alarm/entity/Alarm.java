package com.creavispace.project.domain.alarm.entity;

import com.creavispace.project.domain.common.entity.BaseTimeEntity;
import com.creavispace.project.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;

import static com.creavispace.project.domain.alarm.entity.Alarm.readStatus.READ;

@Getter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Alarm extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String content;

    @ManyToOne(targetEntity = Member.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Enumerated(EnumType.STRING)
    private readStatus readStatus;

    public void updateViewed() {
        this.readStatus = READ;
    }
    public enum readStatus {READ, UNREAD}

}
