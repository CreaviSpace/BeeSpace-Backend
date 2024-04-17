package com.creavispace.project.domain.recruit.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.creavispace.project.domain.recruit.entity.RecruitTechStack;

@Repository
public interface RecruitTechStackRepository extends JpaRepository<RecruitTechStack, Long> {
    public List<RecruitTechStack> findByRecruitId(Long recruitId);
    public void deleteByRecruitId(Long recruitId);

    List<RecruitTechStack> findByRecruitIdIn(List<Long> recruitIds);
}
