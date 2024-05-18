package com.creavispace.project.domain.file.entity;

import com.creavispace.project.domain.recruit.entity.Recruit;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    public RecruitImage(Recruit recruit, String url){
        this.recruit = recruit;
        this.url = url;
    }
}
