package com.creavispace.project.domain.report.service;

import com.creavispace.project.common.dto.response.SuccessResponseDto;
import com.creavispace.project.domain.member.entity.Member;
import com.creavispace.project.domain.member.repository.MemberRepository;
import com.creavispace.project.domain.report.dto.request.ReportRequestDto;
import com.creavispace.project.domain.report.dto.response.ReportResponseDto;
import com.creavispace.project.domain.report.entity.Report;
import com.creavispace.project.domain.report.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService{

    private final ReportRepository reportRepository;
    private final MemberRepository memberRepository;

    @Override
    public SuccessResponseDto<ReportResponseDto> createReport(String memberId, ReportRequestDto dto) {
        ReportResponseDto data = null;

        // 맴버 엔티티 조회
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new NoSuchElementException("로그인 회원 아이디가 존재하지 않습니다."));

        // 신고 생성
        Report report = Report.builder()
            .member(member)
            .category(dto.getCategory())
            .postType(dto.getPostType())
            .postId(dto.getPostId())
            .content(dto.getContent())
            .status(Report.Status.PENDING)
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
        
        // 성공 응답 반환
        return new SuccessResponseDto<>(true, "신고를 완료했습니다.", data);
    }

}
