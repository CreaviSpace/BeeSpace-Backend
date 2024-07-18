package com.creavispace.project.domain.file.entity;

import com.creavispace.project.domain.file.service.ImageManager;
import com.creavispace.project.domain.recruit.entity.Recruit;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Getter
@Entity
public class RecruitImage extends Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recruit_id")
    private Recruit recruit;

    public RecruitImage(String url){
        this.url = url;
    }
    
    public void setRecruit(Recruit recruit){
        this.recruit = recruit;
    }


    public static List<RecruitImage> getUsedImageFilter(List<String> images, String content){
        ImageManager imageManager = new ImageManager();
        List<String> filteredImages = imageManager.usedImageFilter(images, content);

        return filteredImages.stream().map(RecruitImage::new).toList();
    }

}
