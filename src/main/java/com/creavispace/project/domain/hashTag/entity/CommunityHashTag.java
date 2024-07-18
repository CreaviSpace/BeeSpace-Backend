package com.creavispace.project.domain.hashTag.entity;

import com.creavispace.project.common.entity.BaseTimeEntity;
import com.creavispace.project.domain.community.entity.Community;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class CommunityHashTag extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "community_id", nullable = false)
    private Community community;

    private String hashTag;

    public void setCommunity(Community community){
        this.community = community;
    }
}