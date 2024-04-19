package com.creavispace.project.domain.project.service;

import com.creavispace.project.domain.common.dto.response.SuccessResponseDto;
import com.creavispace.project.domain.common.dto.type.ProjectCategory;
import com.creavispace.project.domain.member.entity.Member;
import com.creavispace.project.domain.project.dto.request.ProjectRequestDto;
import com.creavispace.project.domain.project.dto.response.*;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface ProjectService {
    public SuccessResponseDto<ProjectResponseDto> createProject(String memberId, ProjectRequestDto dto);
    public SuccessResponseDto<ProjectResponseDto> modifyProject(String memberId, Long projectId, ProjectRequestDto dto);
    public SuccessResponseDto<ProjectDeleteResponseDto> deleteProject(String memberId, Long projectId);
    public SuccessResponseDto<List<PopularProjectReadResponseDto>> readPopularProjectList();
    public SuccessResponseDto<List<ProjectListReadResponseDto>> readProjectList(Integer size, Integer page, ProjectCategory category);
    public SuccessResponseDto<ProjectReadResponseDto> readProject(String memberId, Long projectId, HttpServletRequest request);

    SuccessResponseDto<List<ProjectListReadResponseDto>> readMyProjectList(String memberId, Integer size, Integer page, String category, String sortType);

    SuccessResponseDto<List<ProjectListReadResponseDto>> readMyProjectFeedBackList(String memberId, Integer size, Integer page, String sortType);
    SuccessResponseDto<List<ProjectListReadResponseDto>> readProjectListForAdmin(Integer size, Integer page, String status, String sortType);
}
