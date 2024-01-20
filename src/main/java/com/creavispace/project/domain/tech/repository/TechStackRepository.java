package com.creavispace.project.domain.tech.repository;

import com.creavispace.project.domain.tech.entity.TechStack;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TechStackRepository extends JpaRepository<TechStack, Long> {
    boolean existsByTechStack(String techStack);

}
