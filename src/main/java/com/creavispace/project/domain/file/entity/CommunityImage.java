package com.creavispace.project.domain.file.entity;

import com.creavispace.project.domain.community.entity.Community;
import com.creavispace.project.domain.file.service.ImageManager;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

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

    public CommunityImage(String url){
        this.url = url;
    }

    public void setCommunity(Community community){ this.community = community; }

    public static List<CommunityImage> getUsedImageFilter(List<String> images, String content){
        ImageManager imageManager = new ImageManager();
        List<String> filteredImages = imageManager.usedImageFilter(images, content);

        return filteredImages.stream().map(CommunityImage::new).toList();
    }

}
