package com.creavispace.project.domain.report.service;

import org.springframework.stereotype.Service;

import com.creavispace.project.domain.common.dto.response.SuccessResponseDto;
import com.creavispace.project.domain.member.entity.Member;
import com.creavispace.project.domain.member.repository.MemberRepository;
import com.creavispace.project.domain.report.dto.request.ReportRequestDto;
import com.creavispace.project.domain.report.dto.response.ReportResponseDto;
import com.creavispace.project.domain.report.entity.Report;
import com.creavispace.project.domain.report.repository.ReportRepository;
import com.creavispace.project.global.exception.CreaviCodeException;
import com.creavispace.project.global.exception.GlobalErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService{

    private final ReportRepository reportRepository;
    private final MemberRepository memberRepository;

    @Override
    public SuccessResponseDto<ReportResponseDto> createReport(Long memberId, ReportRequestDto dto) {
        Member member = memberRepository.findById(memberId).orElseThrow(()-> new CreaviCodeException(GlobalErrorCode.MEMBER_NOT_FOUND));

        Report report = Report.builder()
            .member(member)
            .category(dto.getCategory())
            .postType(dto.getPostType())
            .postId(dto.getPostId())
            .content(dto.getContent())
            .status(Boolean.TRUE)
            .build();

        reportRepository.save(report);
        
        ReportResponseDto create = ReportResponseDto.builder()
            .postId(report.getPostId())
            .postType(report.getPostType())
            .category(report.getCategory())
            .content(report.getContent())
            .build();
        
        return new SuccessResponseDto<>(true, "신고를 완료했습니다.", create);
    }
    
}
