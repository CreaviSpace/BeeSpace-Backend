package com.creavispace.project.domain.project.repository;

import com.creavispace.project.domain.project.entity.ProjectTechStack;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectTechStackRepository extends JpaRepository<ProjectTechStack, Long> {
    public void deleteByProjectId(Long projectId);
    public List<ProjectTechStack> findByProjectId(Long projectId);

}
