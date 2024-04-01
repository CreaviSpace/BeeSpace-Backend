package com.creavispace.project.domain.project.service;

import com.creavispace.project.domain.common.dto.response.SuccessResponseDto;
import com.creavispace.project.domain.common.dto.type.ProjectCategory;
import com.creavispace.project.domain.member.entity.Member;
import java.util.List;

import com.creavispace.project.domain.project.dto.request.ProjectRequestDto;
import com.creavispace.project.domain.project.dto.response.PopularProjectReadResponseDto;
import com.creavispace.project.domain.project.dto.response.ProjectDeleteResponseDto;
import com.creavispace.project.domain.project.dto.response.ProjectListReadResponseDto;
import com.creavispace.project.domain.project.dto.response.ProjectReadResponseDto;
import com.creavispace.project.domain.project.dto.response.ProjectResponseDto;

import jakarta.servlet.http.HttpServletRequest;

public interface ProjectService {
    public SuccessResponseDto<ProjectResponseDto> createProject(Long memberId, ProjectRequestDto dto);
    public SuccessResponseDto<ProjectResponseDto> modifyProject(Long memberId, Long projectId, ProjectRequestDto dto);
    public SuccessResponseDto<ProjectDeleteResponseDto> deleteProject(Long memberId, Long projectId);
    public SuccessResponseDto<List<PopularProjectReadResponseDto>> readPopularProjectList();
    public SuccessResponseDto<List<ProjectListReadResponseDto>> readProjectList(Integer size, Integer page, ProjectCategory category);
    public SuccessResponseDto<ProjectReadResponseDto> readProject(Long memberId, Long projectId, HttpServletRequest request);

    SuccessResponseDto<List<ProjectListReadResponseDto>> readMyProjectList(Member member, Integer size, Integer page, String category);

}
