package com.creavispace.project.domain.hashTag.entity;

import com.creavispace.project.domain.common.entity.BaseTimeEntity;
import com.creavispace.project.domain.community.entity.Community;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class CommunityHashTag extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(targetEntity = Community.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "community_id", nullable = false)
    @JsonBackReference
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Community community;

    @ManyToOne(targetEntity = HashTag.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "hash_tag_id", nullable = false)
    private HashTag hashTag;
}