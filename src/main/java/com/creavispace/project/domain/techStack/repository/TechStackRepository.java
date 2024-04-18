package com.creavispace.project.domain.techStack.repository;

import com.creavispace.project.domain.techStack.entity.TechStack;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TechStackRepository extends JpaRepository<TechStack, String>{
    public List<TechStack> findByTechStackContains(String techStack);
    public List<TechStack> findByTechStackStartingWith(String techStack);
    public List<TechStack> findByTechStack(String techStack);

    List<TechStack> findByTechStackIn(List<String> techStacks);
}
