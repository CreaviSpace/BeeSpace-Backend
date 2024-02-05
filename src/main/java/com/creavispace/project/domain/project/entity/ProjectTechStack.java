package com.creavispace.project.domain.project.entity;

import java.util.ArrayList;
import java.util.List;

import com.creavispace.project.domain.project.dto.request.ProjectTechStackCreateRequestDto;
import com.creavispace.project.domain.project.dto.request.ProjectTechStackDto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class ProjectTechStack {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // @ManyToOne(targetEntity = TechStack.class)
    // @JoinColumn(name = "tech_stack_id", nullable = false, insertable = false, updatable = false)
    // private TechStack techStack;

    @Column(name = "tech_stack_id", nullable = false)
    private Long techStackId;

    @ManyToOne(targetEntity = Project.class)
    @JoinColumn(name = "project_id", nullable = false, insertable = false, updatable = false)
    private Project project;

    @Column(name = "project_id", nullable = false)
    private Long projectId;

    public ProjectTechStack(ProjectTechStackCreateRequestDto dto, Long projectId){
        this.techStackId = dto.getTechStackId();
        this.projectId = dto.getProjectId();
    }

    public ProjectTechStack(ProjectTechStackDto dto, Long projectId){
        this.id = dto.getId();
        this.techStackId = dto.getTechStackId();
        this.projectId = projectId;
    }

    public static List<ProjectTechStack> copyList(List<ProjectTechStackDto> techStackList, Long projectId){
        List<ProjectTechStack> list = new ArrayList<>();
        if(techStackList == null) return list;
        for(ProjectTechStackDto dto : techStackList){
            list.add(new ProjectTechStack(dto, projectId));
        }
        return list;
    }
}