package com.creavispace.project.domain.file.entity;

import com.creavispace.project.domain.file.service.ImageManager;
import com.creavispace.project.domain.project.entity.Project;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Builder
@Entity
public class ProjectImage extends Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    public ProjectImage(String url){
        this.url = url;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public static List<ProjectImage> getUsedImageFilter(List<String> images, String content){
        ImageManager imageManager = new ImageManager();
        List<String> filteredImages = imageManager.usedImageFilter(images, content);

        return filteredImages.stream().map(ProjectImage::new).toList();
    }




}
