package com.creavispace.project.domain.project.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.creavispace.project.domain.project.dto.request.ProjectCreateRequestDto;
import com.creavispace.project.domain.project.dto.request.ProjectModifyRequestDto;

@Service
public interface ProjectService {
    public ResponseEntity createProject(ProjectCreateRequestDto dto);
    public ResponseEntity modifyProject(ProjectModifyRequestDto dto);
    public ResponseEntity deleteProject(long projectId);
    public ResponseEntity readPopularProjectList();
    public ResponseEntity readProjectList(int size, int page);
    public ResponseEntity readProject(long projectId);
}
