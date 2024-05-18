package com.creavispace.project.domain.file.entity;

import com.creavispace.project.domain.community.entity.Community;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@Entity
public class CommunityImage extends Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "community_id")
    private Community community;

    public CommunityImage(Community community, String url){
        this.community = community;
        this.url = url;
    }
}
