package com.creavispace.project.domain.admin.repository;

import com.creavispace.project.domain.admin.entity.FiredMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FiredMemberRepository extends JpaRepository<FiredMember, String> {
}
