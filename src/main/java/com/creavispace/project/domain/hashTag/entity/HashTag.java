package com.creavispace.project.domain.hashTag.entity;

import java.util.List;

import com.creavispace.project.domain.common.entity.BaseTimeEntity;
import com.creavispace.project.domain.community.entity.CommunityHashTag;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class HashTag extends BaseTimeEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String hashTag;

    @OneToMany(mappedBy = "hashTag")
    private List<CommunityHashTag> communityHashTags;
}
