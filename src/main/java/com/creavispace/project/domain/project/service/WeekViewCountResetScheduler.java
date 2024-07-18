package com.creavispace.project.domain.project.service;

import com.creavispace.project.domain.project.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WeekViewCountResetScheduler {
    
    private final ProjectRepository projectRepository;

    @Scheduled(cron = "0 0 0 * * MON")
    public void resetWeeklyViewCount() {
        projectRepository.resetWeekViewCount();
    }
}
