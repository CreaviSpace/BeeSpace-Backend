package com.creavispace.project.domain.report.repository;

import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.creavispace.project.domain.report.entity.Report;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long>{
    List<Report> findBy(Pageable pageable);
}
