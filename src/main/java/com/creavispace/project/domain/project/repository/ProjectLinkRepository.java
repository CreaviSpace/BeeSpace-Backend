package com.creavispace.project.domain.project.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.creavispace.project.domain.project.entity.ProjectLink;

public interface ProjectLinkRepository extends JpaRepository<ProjectLink, Long>{
    public List<ProjectLink> findByProjectId(Long projectId);
    public void deleteByProjectId(Long projectId);

    List<ProjectLink> findByProjectIdIn(List<Long> collect);
}
