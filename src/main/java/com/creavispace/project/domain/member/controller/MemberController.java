package com.creavispace.project.domain.member.controller;

import com.creavispace.project.domain.bookmark.entity.Bookmark;
import com.creavispace.project.domain.bookmark.service.BookmarkService;
import com.creavispace.project.domain.comment.dto.response.CommentResponseDto;
import com.creavispace.project.domain.comment.service.CommentService;
import com.creavispace.project.domain.common.dto.response.SuccessResponseDto;
import com.creavispace.project.domain.community.dto.response.CommunityResponseDto;
import com.creavispace.project.domain.community.service.CommunityService;
import com.creavispace.project.domain.feedback.service.FeedbackService;
import com.creavispace.project.domain.member.dto.response.DataResponseDto;
import com.creavispace.project.domain.member.dto.response.MemberResponseDto;
import com.creavispace.project.domain.member.entity.Member;
import com.creavispace.project.domain.member.service.MemberService;
import com.creavispace.project.domain.project.dto.response.ProjectListReadResponseDto;
import com.creavispace.project.domain.project.service.ProjectService;
import com.creavispace.project.domain.recruit.dto.response.RecruitListReadResponseDto;
import com.creavispace.project.domain.recruit.service.RecruitService;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.creavispace.project.global.util.UsableConst.MEMBER_ID;
import static com.creavispace.project.global.util.UsableConst.SORT_TYPE;

@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {

    @Value("${jwt.secret}")
    private String jwtSecret;

    private final MemberService memberService;
    private final ProjectService projectService;
    private final RecruitService recruitService;
    private final CommunityService communityService;
    private final BookmarkService bookmarkService;
    private final CommentService commentService;
    private final FeedbackService feedbackService;

    @GetMapping("/read/profile")
    @Operation(summary = "사용자 아이디로 사용자 프로필 조회")
    public ResponseEntity<MemberResponseDto> readMember(@RequestParam(MEMBER_ID) String memberId) {
        System.out.println("MemberController.readMember");
        Member member = memberService.findById(memberId);
        return ResponseEntity.ok().body(new MemberResponseDto(member));
    }

    @GetMapping("/read/contents/project")
    @Operation(summary = "사용자 아이디와 일치하는 프로젝트 게시물 조회")
    public ResponseEntity<SuccessResponseDto<List<ProjectListReadResponseDto>>> readMemberProjectContents(
            @RequestParam(MEMBER_ID) String memberId, @RequestParam Integer page, @RequestParam Integer size,
            @RequestParam(SORT_TYPE) String sortType) {
        System.out.println("MemberController.readMemberProjectContents");
        SuccessResponseDto<List<ProjectListReadResponseDto>> memberProjectContents = projectService.readMyProjectList(
                memberId, size, page, sortType);
        return ResponseEntity.ok().body(memberProjectContents);
    }

    @GetMapping("/read/contents/recruit")
    @Operation(summary = "사용자 아이디와 일치하는 모집 게시물 조회")
    public ResponseEntity<SuccessResponseDto<List<RecruitListReadResponseDto>>> readMemberRecruitContents(
            @RequestParam(MEMBER_ID) String memberId, @RequestParam Integer page, @RequestParam Integer size,
            @RequestParam(SORT_TYPE) String sortType) {

        SuccessResponseDto<List<RecruitListReadResponseDto>> memberRecruitContents = recruitService.readMyRecruitList(
                memberId, size, page, sortType);

        return ResponseEntity.ok().body(memberRecruitContents);
    }

    @GetMapping("/read/contents/community")
    @Operation(summary = "사용자 아이디와 일치하는 커뮤니티 게시물 조회")
    public ResponseEntity<SuccessResponseDto<List<CommunityResponseDto>>> readMemberCommunityContents(
            @RequestParam(MEMBER_ID) String memberId, @RequestParam Integer page, @RequestParam Integer size,
            @RequestParam(SORT_TYPE) String sortType) {

        SuccessResponseDto<List<CommunityResponseDto>> memberCommunityContents = communityService.readMyCommunityList(
                memberId, size, page, sortType);
        return ResponseEntity.ok().body(memberCommunityContents);
    }

    @GetMapping("/read/bookmark")
    @Operation(summary = " 사용자 아이디로 사용자가 북마크한 게시물 검색, sortType = asc or desc 대 소문자 구분 안함")
    public ResponseEntity<SuccessResponseDto<List<Bookmark>>> readMemberBookmarkContents(
            @RequestParam(MEMBER_ID) String memberId, @RequestParam Integer page, @RequestParam Integer size,
            @RequestParam String category, @RequestParam(SORT_TYPE) String sortType) throws JsonProcessingException {
        SuccessResponseDto<List<Bookmark>> listSuccessResponseDto = bookmarkService.readMyBookmark(memberId, page, size,
                category, sortType);

        return ResponseEntity.ok().body(listSuccessResponseDto);
    }

    @GetMapping("/read/feedback")
    @Operation(summary = "사용자 아이디로 사용자 피드백 조회")
    public ResponseEntity<SuccessResponseDto<List<ProjectListReadResponseDto>>> readMemberFeedbackContents(
            @RequestParam(MEMBER_ID) String memberId, @RequestParam Integer page, @RequestParam Integer size,
            @RequestParam(SORT_TYPE) String sortType) {
        SuccessResponseDto<List<ProjectListReadResponseDto>> memberProjectContents = projectService.readMyProjectFeedBackList(
                memberId, size, page, sortType);
        return ResponseEntity.ok().body(memberProjectContents);
    }


    @GetMapping("/read/comment")
    @Operation(summary = "사용자 아이디로 사용자 댓글 조회")
    public ResponseEntity<SuccessResponseDto<List<CommentResponseDto>>> readMemberComment(
            @RequestParam(MEMBER_ID) String memberId, @RequestParam Integer page, @RequestParam Integer size,
            @RequestParam String category, @RequestParam(SORT_TYPE) String sortType) {
        SuccessResponseDto<List<CommentResponseDto>> listSuccessResponseDto = commentService.readMyContentsList(
                memberId,
                page, size, category, sortType);

        return ResponseEntity.ok().body(listSuccessResponseDto);
    }

    @GetMapping("/search")
    @Operation(summary = "닉네임 또는 아이디 태그를 포함하는 사용자 검색")
    public DataResponseDto findMember(@RequestParam String search) {
        List<MemberResponseDto> userData = memberService.findByMemberNicknameOrIdContaining(search);

        return new DataResponseDto(userData);
    }

    @PostMapping("/expire")
    @Operation(summary = "회원 탈퇴")
    public void expireMember(@RequestBody(required = true) String jwt) {
        memberService.expireMember(jwt);
    }
}
