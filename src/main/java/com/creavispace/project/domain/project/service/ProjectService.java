package com.creavispace.project.domain.project.service;

import com.creavispace.project.common.dto.response.SuccessResponseDto;
import com.creavispace.project.domain.project.dto.request.ProjectRequestDto;
import com.creavispace.project.domain.project.dto.response.PopularProjectReadResponseDto;
import com.creavispace.project.domain.project.dto.response.ProjectListReadResponseDto;
import com.creavispace.project.domain.project.dto.response.ProjectReadResponseDto;
import com.creavispace.project.domain.project.entity.Project;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProjectService {
    public SuccessResponseDto<Long> createProject(String memberId, ProjectRequestDto dto);
    public SuccessResponseDto<Long> modifyProject(String memberId, Long projectId, ProjectRequestDto dto);
    public SuccessResponseDto<Long> deleteProject(String memberId, Long projectId);
    public SuccessResponseDto<List<PopularProjectReadResponseDto>> readPopularProjectList();
    public SuccessResponseDto<List<ProjectListReadResponseDto>> readProjectList(Pageable pageRequest, Project.Category category);
    public SuccessResponseDto<ProjectReadResponseDto> readProject(String memberId, Long projectId);
    public SuccessResponseDto<List<ProjectListReadResponseDto>> readProjectListByMemberId(Pageable pageRequest, Project.Category category, String memberId, String memberId_param);

}
