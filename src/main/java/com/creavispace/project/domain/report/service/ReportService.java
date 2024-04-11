package com.creavispace.project.domain.report.service;

import com.creavispace.project.domain.common.dto.response.SuccessResponseDto;
import com.creavispace.project.domain.report.dto.request.ReportRequestDto;
import com.creavispace.project.domain.report.dto.response.ReportResponseDto;
import com.creavispace.project.domain.report.entity.Report;
import java.util.List;

public interface ReportService {
    public SuccessResponseDto<ReportResponseDto> createReport(String memberId, ReportRequestDto dto);
    List<Report> readReportList(Integer size, Integer page, String sortType);
}
