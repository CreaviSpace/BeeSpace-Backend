package com.creavispace.project.domain.community.entity;

import com.creavispace.project.common.dto.type.PostType;
import com.creavispace.project.common.post.entity.Post;
import com.creavispace.project.domain.community.dto.request.CommunityRequestDto;
import com.creavispace.project.domain.file.entity.CommunityImage;
import com.creavispace.project.domain.hashTag.entity.HashTag;
import com.creavispace.project.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@ToString
public class Community extends Post {

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CommunityCategory category;

    @Builder.Default
    @OneToMany(mappedBy = "community", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CommunityImage> communityImages = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "community", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HashTag> hashTags = new ArrayList<>();

    public void update(CommunityRequestDto dto) {
        super.changeTitleAndContentAndThumbnailAndBannerContent(dto.getTitle(), dto.getContent(),null,null);
        this.category = dto.getCategory();
    }

    //== 연관관계 메서드 ==//
    public void addCommunityImages(CommunityImage communityImage){
        communityImages.add(communityImage);
        communityImage.setCommunity(this);
    }

    public void addCommunityHashTag(HashTag hashTag){
        hashTags.add(hashTag);
        hashTag.setCommunity(this);
    }

    //== 생성 메서드 ==//
    public static Community createCommunity(CommunityRequestDto dto, Member member, List<CommunityImage> communityImages, List<HashTag> hashTags) {
        Community community = Community.builder()
                .category(dto.getCategory())
                .build();
        community.setup(PostType.COMMUNITY, member, dto.getTitle(), dto.getContent(), null, null);
        for(CommunityImage communityImage : communityImages){
            community.addCommunityImages(communityImage);
        }
        for(HashTag hashTag : hashTags){
            community.addCommunityHashTag(hashTag);
        }
        return community;
    }


}
