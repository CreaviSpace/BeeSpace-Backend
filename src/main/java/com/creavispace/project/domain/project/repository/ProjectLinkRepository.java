package com.creavispace.project.domain.project.repository;

import java.util.List;

import com.creavispace.project.domain.project.entity.Link;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectLinkRepository extends JpaRepository<Link, Long>{
    public List<Link> findByProjectId(Long projectId);
    public void deleteByProjectId(Long projectId);

    List<Link> findByProjectIdIn(List<Long> collect);
}
