package com.creavispace.project.domain.project.service;

import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.creavispace.project.domain.project.entity.Project;
import com.creavispace.project.domain.project.repository.ProjectRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WeekViewCountResetScheduler {
    
    private final ProjectRepository projectRepository;

    @Scheduled(cron = "0 0 0 * * MON")
    public void resetWeeklyViewCount() {
        List<Project> projects = projectRepository.findAll();
        for(Project project : projects){
            project.resetWeekViewCount();
        }
        projectRepository.saveAll(projects);
    }
}
