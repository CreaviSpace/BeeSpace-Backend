package com.creavispace.project.domain.mypage.controller;

import com.creavispace.project.common.dto.response.SuccessResponseDto;
import com.creavispace.project.common.dto.type.PostType;
import com.creavispace.project.domain.comment.service.CommentService;
import com.creavispace.project.domain.community.dto.response.CommunityResponseDto;
import com.creavispace.project.domain.community.entity.CommunityCategory;
import com.creavispace.project.domain.community.service.CommunityService;
import com.creavispace.project.domain.mypage.dto.response.MyPageCommentResponseDto;
import com.creavispace.project.domain.project.dto.response.ProjectListReadResponseDto;
import com.creavispace.project.domain.project.entity.Project;
import com.creavispace.project.domain.project.service.ProjectService;
import com.creavispace.project.domain.recruit.dto.response.RecruitListReadResponseDto;
import com.creavispace.project.domain.recruit.entity.Recruit;
import com.creavispace.project.domain.recruit.service.RecruitService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/mypage")
@RequiredArgsConstructor
public class MyPageController {

    private final ProjectService projectService;
    private final RecruitService recruitService;
    private final CommunityService communityService;
    private final CommentService commentService;

    @GetMapping("/post/project")
    @Operation(summary = "마이페이지 프로젝트 리스트 조회")
    public ResponseEntity<SuccessResponseDto<List<ProjectListReadResponseDto>>> mypagePostProjectList(
            @AuthenticationPrincipal String memberId,
            @SortDefault(sort = {"id"}, direction = Sort.Direction.DESC) Pageable pageRequest,
            @RequestParam("memberId") String memberId_param,
            @RequestParam("category") Project.Category category
    ) {
        return ResponseEntity.ok().body(projectService.readProjectListByMemberId(pageRequest, category, memberId, memberId_param));
    }

    @GetMapping("/post/recruit")
    @Operation(summary = "마이페이지 모집 리스트 조회")
    public ResponseEntity<SuccessResponseDto<List<RecruitListReadResponseDto>>> mypagePostRecruitList(
            @AuthenticationPrincipal String memberId,
            @SortDefault(sort = {"id"}, direction = Sort.Direction.DESC) Pageable pageRequest,
            @RequestParam("memberId") String memberId_param,
            @RequestParam("category") Recruit.Category category
    ) {
        return ResponseEntity.ok().body(recruitService.readRecruitListByMemberId(pageRequest, category, memberId, memberId_param));
    }

    @GetMapping("/post/community")
    @Operation(summary = "마이페이지 커뮤니티 리스트 조회")
    public ResponseEntity<SuccessResponseDto<List<CommunityResponseDto>>> mypagePostCommunityList(
            @AuthenticationPrincipal String memberId,
            @SortDefault(sort = {"id"}, direction = Sort.Direction.DESC) Pageable pageRequest,
            @RequestParam("memberId") String memberId_param,
            @RequestParam("category") CommunityCategory category
    ) {
        return ResponseEntity.ok().body(communityService.mypageCommunityList(pageRequest, category, memberId, memberId_param));
    }

    @GetMapping("/comment")
    @Operation(summary = "마이페이지 내가 작성한 댓글 프로젝트 리스트 조회")
    public ResponseEntity<SuccessResponseDto<List<MyPageCommentResponseDto>>> mypageCommentProjectList(
            @AuthenticationPrincipal String memberId,
            @RequestParam("postType") PostType postType,
            @SortDefault(sort = {"id"}, direction = Sort.Direction.DESC) Pageable pageable
            ){
        return ResponseEntity.ok().body(commentService.mypageCommentPost(memberId, postType, pageable));
    }



}
