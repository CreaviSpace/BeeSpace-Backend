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

    @GetMapping("/")
    @Operation(summary = "사용자 아이디로 사용자 프로필 조회")
    public ResponseEntity<SuccessResponseDto<MemberResponseDto>> readMember(@RequestParam("member-id") String memberId) {
        return ResponseEntity.ok().body(memberService.readMember(memberId));
    }

    @DeleteMapping("/")
    @Operation(summary = "회원 탈퇴")
    public void withdrawn(@AuthenticationPrincipal String memberId) {
        memberService.withdrawn(memberId);
    }

    @GetMapping("/search")
    @Operation(summary = "닉네임 또는 아이디 태그를 포함하는 사용자 검색")
    public ResponseEntity<SuccessResponseDto<List<SearchMemberResponseDto>>> searchMember(@RequestParam("text") String text) {
        return ResponseEntity.ok().body(memberService.searchMember(text));
    }
//
//    @GetMapping("/read/bookmark")
//    @Operation(summary = " 사용자 아이디로 사용자가 북마크한 게시물 검색, sortType = asc or desc 대 소문자 구분 안함")
//    public ResponseEntity<SuccessResponseDto<List<BookmarkContentsResponseDto>>> readMemberBookmarkContents(
//            @RequestParam(MEMBER_ID) String memberId, @RequestParam Integer page, @RequestParam Integer size,
//            @RequestParam String category, @RequestParam(SORT_TYPE) String sortType) throws JsonProcessingException {
//        SuccessResponseDto<List<BookmarkContentsResponseDto>> listSuccessResponseDto = bookmarkService.readMyBookmark(memberId, page, size,
//                category, sortType);
//
//        return ResponseEntity.ok().body(listSuccessResponseDto);
//    }
//
//    @GetMapping("/read/feedback")
//    @Operation(summary = "사용자 아이디로 사용자 피드백 조회")
//    public ResponseEntity<SuccessResponseDto<List<ProjectListReadResponseDto>>> readMemberFeedbackContents(
//            @RequestParam(MEMBER_ID) String memberId, @RequestParam Integer page, @RequestParam Integer size,
//            @RequestParam(SORT_TYPE) String sortType) {
//        SuccessResponseDto<List<ProjectListReadResponseDto>> memberProjectContents = projectService.readMyProjectFeedBackList(
//                memberId, size, page, sortType);
//        return ResponseEntity.ok().body(memberProjectContents);

//    }


}
