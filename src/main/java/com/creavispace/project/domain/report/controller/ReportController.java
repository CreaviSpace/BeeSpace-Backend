package com.creavispace.project.domain.report.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.creavispace.project.domain.common.dto.response.SuccessResponseDto;
import com.creavispace.project.domain.report.dto.request.ReportRequestDto;
import com.creavispace.project.domain.report.dto.response.ReportResponseDto;
import com.creavispace.project.domain.report.service.ReportService;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/report")
public class ReportController {
    
    private final ReportService reportService;

    private final static String CREATE_REPORT = "";

    @PostMapping(CREATE_REPORT)
    @Operation(summary = "신고하기")
    public ResponseEntity<SuccessResponseDto<ReportResponseDto>> createReport(
        @AuthenticationPrincipal Long memberId,
        @RequestBody ReportRequestDto requestBody
    ){
        log.info("/report/controller : 신고하기");
        return ResponseEntity.ok().body(reportService.createReport(memberId, requestBody));
    }
}
