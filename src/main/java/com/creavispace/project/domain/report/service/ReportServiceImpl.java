package com.creavispace.project.domain.report.service;

import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.creavispace.project.common.dto.response.SuccessResponseDto;
import com.creavispace.project.common.dto.type.PostType;
import com.creavispace.project.common.dto.type.ReportCategory;
import com.creavispace.project.domain.member.entity.Member;
import com.creavispace.project.domain.member.repository.MemberRepository;
import com.creavispace.project.domain.report.dto.request.ReportRequestDto;
import com.creavispace.project.domain.report.dto.response.ReportResponseDto;
import com.creavispace.project.domain.report.entity.Report;
import com.creavispace.project.domain.report.repository.ReportRepository;
import com.creavispace.project.common.exception.CreaviCodeException;
import com.creavispace.project.common.exception.GlobalErrorCode;
import com.creavispace.project.common.utils.CustomValueOf;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService{

    private final ReportRepository reportRepository;
    private final MemberRepository memberRepository;

    @Override
    public SuccessResponseDto<ReportResponseDto> createReport(String memberId, ReportRequestDto dto) {
        ReportResponseDto data = null;

        // 회원이 존재하는지
        Member member = memberRepository.findById(memberId).orElseThrow(()-> new CreaviCodeException(GlobalErrorCode.MEMBER_NOT_FOUND));

        ReportCategory category = CustomValueOf.valueOf(ReportCategory.class, dto.getCategory(), GlobalErrorCode.NOT_FOUND_REPORT_CATEGORY);
        PostType postType = CustomValueOf.valueOf(PostType.class, dto.getPostType(), GlobalErrorCode.NOT_FOUND_POST_TYPE);

        // 신고 생성
        Report report = Report.builder()
            .member(member)
            .category(category)
            .postType(postType)
            .postId(dto.getPostId())
            .content(dto.getContent())
            .status(Boolean.TRUE)
            .build();

        // 신고 저장
        reportRepository.save(report);
        
        // 신고 DTO
        data = ReportResponseDto.builder()
            .postId(report.getPostId())
            .postType(report.getPostType().name())
            .category(report.getCategory().name())
            .content(report.getContent())
            .build();
        
        log.info("/report/service : createReport success data = {}", data);
        // 성공 응답 반환
        return new SuccessResponseDto<>(true, "신고를 완료했습니다.", data);
    }

    @Override
    public List<Report> readReportList(Integer size, Integer page, String sortType) {
        Pageable pageable = pageable(size, page, sortType);
        return reportRepository.findBy(pageable);
    }

    private Pageable pageable(Integer size, Integer page, String sortType) {
        if (sortType.toLowerCase().equals("asc")) {
            return PageRequest.of(page - 1, size, Sort.by("createdDate").ascending());
        }
        if (sortType.toLowerCase().equals("desc")) {
            return PageRequest.of(page - 1, size, Sort.by("createdDate").descending());
        }
        return PageRequest.of(page - 1, size, Sort.by("createdDate").descending());
    }

}
