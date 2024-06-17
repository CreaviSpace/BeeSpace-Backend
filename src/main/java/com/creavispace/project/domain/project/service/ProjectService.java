package com.creavispace.project.domain.project.service;

import com.creavispace.project.domain.admin.dto.DailySummary;
import com.creavispace.project.domain.admin.dto.MonthlySummary;
import com.creavispace.project.domain.admin.dto.YearlySummary;
import com.creavispace.project.common.dto.response.SuccessResponseDto;
import com.creavispace.project.common.dto.type.ProjectCategory;
import com.creavispace.project.domain.project.dto.request.ProjectRequestDto;
import com.creavispace.project.domain.project.dto.response.*;
import com.creavispace.project.common.Service;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProjectService extends Service {
    public SuccessResponseDto<ProjectResponseDto> createProject(String memberId, ProjectRequestDto dto);
    public SuccessResponseDto<ProjectResponseDto> modifyProject(String memberId, Long projectId, ProjectRequestDto dto);
    public SuccessResponseDto<ProjectDeleteResponseDto> deleteProject(String memberId, Long projectId);

    @Transactional
    SuccessResponseDto<ProjectDeleteResponseDto> deleteMemberProject(String memberId);

    public SuccessResponseDto<List<PopularProjectReadResponseDto>> readPopularProjectList();
    public SuccessResponseDto<List<ProjectListReadResponseDto>> readProjectList(Pageable pageRequest, ProjectCategory category);
    public SuccessResponseDto<ProjectReadResponseDto> readProject(String memberId, Long projectId, HttpServletRequest request);

    SuccessResponseDto<List<ProjectListReadResponseDto>> readMyProjectList(String memberId, Integer size, Integer page, String category, String sortType);

    SuccessResponseDto<List<ProjectListReadResponseDto>> readMyProjectFeedBackList(String memberId, Integer size, Integer page, String sortType);
    SuccessResponseDto<List<ProjectListReadResponseDto>> readProjectListForAdmin(Integer size, Integer page, String status, String sortType);

    SuccessResponseDto<List<MonthlySummary>> countMonthlySummary(int year);

    SuccessResponseDto<List<YearlySummary>> countYearlySummary();

    SuccessResponseDto<List<DailySummary>> countDailySummary(int year, int month);

}
