package com.creavispace.project.domain.report.service;

import com.creavispace.project.domain.common.dto.response.SuccessResponseDto;
import com.creavispace.project.domain.report.dto.request.ReportRequestDto;
import com.creavispace.project.domain.report.dto.response.ReportResponseDto;

public interface ReportService {
    public SuccessResponseDto<ReportResponseDto> createReport(String memberId, ReportRequestDto dto);
}
