package com.creavispace.project.domain.mypage.controller;

import com.creavispace.project.config.auth.utils.JwtUtil;
import com.creavispace.project.domain.member.dto.response.MemberJwtResponseDto;
import com.creavispace.project.domain.member.entity.Member;
import com.creavispace.project.domain.member.service.MemberService;
import com.creavispace.project.domain.mypage.dto.request.MyPageModifyRequestDto;
import com.creavispace.project.domain.mypage.dto.response.MyPageProfileResponseDto;
import com.creavispace.project.domain.project.service.ProjectService;
import com.creavispace.project.domain.recruit.service.RecruitService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
public class MyPageController {

    private final MemberService memberService;
    private final ProjectService projectService;
    private final RecruitService recruitService;


    @Value("${jwt.secret}")
    private String secretKey;

    @PostMapping("mypage/edit")
    @Operation(summary = "마이페이지 수정")
    public ResponseEntity<MyPageProfileResponseDto> modifyProfile(HttpServletRequest request,
                                                                  @RequestBody MyPageModifyRequestDto requestDto) {
        Optional<Member> member = getMember(request);

        if (member.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MyPageProfileResponseDto());
        }

        Member update = memberService.update(member.get(), requestDto);

        return ResponseEntity.status(HttpStatus.OK).body(new MyPageProfileResponseDto(update));
    }

    private Optional<Member> getMember(HttpServletRequest request) {
        final String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);

        MemberJwtResponseDto memberInfo = JwtUtil.getUserInfo(authorization, secretKey);
        Optional<Member> member = memberService.findByMemberIdTag(memberInfo.memberIdTag());
        return member;
    }
}
