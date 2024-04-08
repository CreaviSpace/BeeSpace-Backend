package com.creavispace.project.domain.project.controller;

import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.creavispace.project.domain.common.dto.response.SuccessResponseDto;
import com.creavispace.project.domain.common.dto.type.ProjectCategory;
import com.creavispace.project.domain.project.dto.request.ProjectRequestDto;
import com.creavispace.project.domain.project.dto.response.PopularProjectReadResponseDto;
import com.creavispace.project.domain.project.dto.response.ProjectResponseDto;
import com.creavispace.project.domain.project.dto.response.ProjectDeleteResponseDto;
import com.creavispace.project.domain.project.dto.response.ProjectListReadResponseDto;
import com.creavispace.project.domain.project.dto.response.ProjectReadResponseDto;
import com.creavispace.project.domain.project.service.ProjectService;
import com.creavispace.project.global.exception.GlobalErrorCode;
import com.creavispace.project.global.util.CustomValueOf;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/project")
public class ProjectController {

    private final ProjectService projectService;
    
    private static final String CREATE_PROJECT = "";
    private static final String MODIFY_PROJECT = "/{projectId}";
    private static final String DELETE_PROJECT = "/{projectId}";
    private static final String READ_POPULAR_PROJECT = "/popular";
    private static final String READ_PROJECT_LIST = "";
    private static final String READ_PROJECT = "/{projectId}";

    @PostMapping(CREATE_PROJECT)
    @Operation(summary = "프로젝트 게시글 생성")
    public ResponseEntity<SuccessResponseDto<ProjectResponseDto>> createProject(
        @AuthenticationPrincipal String memberId,
        @RequestBody ProjectRequestDto dto
    ) {
        log.info("/project/controller : 프로젝트 게시글 생성");
        return ResponseEntity.ok().body(projectService.createProject(memberId, dto));
    }

    @PutMapping(MODIFY_PROJECT)
    @Operation(summary = "프로젝트 게시글 수정")
    public ResponseEntity<SuccessResponseDto<ProjectResponseDto>> modifyProject(
        @AuthenticationPrincipal String memberId,
        @PathVariable("projectId") Long projectId, 
        @RequestBody ProjectRequestDto dto
    ) {
        log.info("/project/controller : 프로젝트 게시글 수정");
        return ResponseEntity.ok().body(projectService.modifyProject(memberId, projectId, dto));
    }
    
    @DeleteMapping(DELETE_PROJECT)
    @Operation(summary = "프로젝트 게시글 삭제")
    public ResponseEntity<SuccessResponseDto<ProjectDeleteResponseDto>> deleteProject(
        @AuthenticationPrincipal String memberId,
        @PathVariable("projectId") Long projectId
    ) {
        log.info("/project/controller : 프로젝트 게시글 삭제");
        return ResponseEntity.ok().body(projectService.deleteProject(memberId, projectId));
    }

    @GetMapping(READ_POPULAR_PROJECT)
    @Operation(summary = "프로젝트 인기 게시글 6개 조회 / 프로젝트 베너 조회")
    public ResponseEntity<SuccessResponseDto<List<PopularProjectReadResponseDto>>> readPopularProjectList() {
        log.info("/project/controller : 프로젝트 인기 게시글 6개 조회 / 프로젝트 베너 조회");
        return ResponseEntity.ok().body(projectService.readPopularProjectList());
    }

    @GetMapping(READ_PROJECT_LIST)
    @Operation(summary = "프로젝트 게시글 리스트 조회")
    public ResponseEntity<SuccessResponseDto<List<ProjectListReadResponseDto>>> readProjectList(
        @RequestParam(value = "size", required = false, defaultValue = "6") Integer size,
        @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
        @RequestParam(value = "category", required = false) String category
    ) {
        log.info("/project/controller : 프로젝트 게시글 리스트 조회");
        ProjectCategory projectCategory = null;
        if(category != null){
            projectCategory = CustomValueOf.valueOf(ProjectCategory.class, category, GlobalErrorCode.NOT_FOUND_PROJECT_CATEGORY);
        }
        return ResponseEntity.ok().body(projectService.readProjectList(size, page, projectCategory));
    }

    @GetMapping(READ_PROJECT)
    @Operation(summary = "프로젝트 게시글 디테일")
    public ResponseEntity<SuccessResponseDto<ProjectReadResponseDto>> readProject(
        @AuthenticationPrincipal String memberId,
        @PathVariable("projectId") Long projectId,
        HttpServletRequest request
    ) {
        log.info("/project/controller : 프로젝트 게시글 디테일");
        return ResponseEntity.ok().body(projectService.readProject(memberId, projectId, request));
    }
    
}
