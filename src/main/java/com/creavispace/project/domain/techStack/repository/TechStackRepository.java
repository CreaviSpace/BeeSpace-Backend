package com.creavispace.project.domain.techStack.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.creavispace.project.domain.techStack.entity.TechStack;

@Repository
public interface TechStackRepository extends JpaRepository<TechStack, Long>{
    public List<TechStack> findByTechStackContains(String techStack);
    public List<TechStack> findByTechStackStartingWith(String techStack);
    public List<TechStack> findByTechStack(String techStack);

    List<TechStack> findByIdIn(List<Long> collect);
}
