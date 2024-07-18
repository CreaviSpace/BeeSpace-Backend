package com.creavispace.project.domain.member.controller;

import com.creavispace.project.common.dto.response.SuccessResponseDto;
import com.creavispace.project.domain.member.dto.response.MemberResponseDto;
import com.creavispace.project.domain.member.dto.response.SearchMemberResponseDto;
import com.creavispace.project.domain.member.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/{memberId}")
    @Operation(summary = "사용자 아이디로 사용자 프로필 조회")
    public ResponseEntity<SuccessResponseDto<MemberResponseDto>> readMember(@PathVariable("memberId") String memberId) {
        return ResponseEntity.ok().body(memberService.readMember(memberId));
    }

    @DeleteMapping("")
    @Operation(summary = "회원 탈퇴")
    public void withdrawn(@AuthenticationPrincipal String memberId) {
        memberService.withdrawn(memberId);
    }

    @GetMapping("/search")
    @Operation(summary = "닉네임 또는 아이디 태그를 포함하는 사용자 검색")
    public ResponseEntity<SuccessResponseDto<List<SearchMemberResponseDto>>> searchMember(@RequestParam("text") String text) {
        return ResponseEntity.ok().body(memberService.searchMember(text));
    }



}
