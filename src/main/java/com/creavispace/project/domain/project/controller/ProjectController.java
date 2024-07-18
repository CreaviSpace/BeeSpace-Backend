package com.creavispace.project.domain.project.controller;

import com.creavispace.project.common.dto.response.SuccessResponseDto;
import com.creavispace.project.domain.project.dto.request.ProjectRequestDto;
import com.creavispace.project.domain.project.dto.response.PopularProjectReadResponseDto;
import com.creavispace.project.domain.project.dto.response.ProjectListReadResponseDto;
import com.creavispace.project.domain.project.dto.response.ProjectReadResponseDto;
import com.creavispace.project.domain.project.entity.Project;
import com.creavispace.project.domain.project.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;
    
    private static final String CREATE_PROJECT = "/project";
    private static final String MODIFY_PROJECT = "/project/{projectId}";
    private static final String DELETE_PROJECT = "/project/{projectId}";
    private static final String READ_POPULAR_PROJECT = "/project/popular";
    private static final String READ_PROJECT_LIST = "/project";
    private static final String READ_PROJECT = "/project/{projectId}";
    private static final String MYPAGE_READ_PROJECT_LIST = "/mypage/project/{projectId}";

    @PostMapping(CREATE_PROJECT)
    @Operation(summary = "프로젝트 게시글 생성")
    public ResponseEntity<SuccessResponseDto<Long>> createProject(
        @AuthenticationPrincipal String memberId,
        @RequestBody ProjectRequestDto dto
    ) {
        log.info("/project/controller : 프로젝트 게시글 생성");
        return ResponseEntity.ok().body(projectService.createProject(memberId, dto));
    }

    @PutMapping(MODIFY_PROJECT)
    @Operation(summary = "프로젝트 게시글 수정")
    public ResponseEntity<SuccessResponseDto<Long>> modifyProject(
        @AuthenticationPrincipal String memberId,
        @PathVariable("projectId") Long projectId, 
        @RequestBody ProjectRequestDto dto
    ) {
        log.info("/project/controller : 프로젝트 게시글 수정");
        return ResponseEntity.ok().body(projectService.modifyProject(memberId, projectId, dto));
    }
    
    @DeleteMapping(DELETE_PROJECT)
    @Operation(summary = "프로젝트 게시글 삭제")
    public ResponseEntity<SuccessResponseDto<Long>> deleteProject(
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
        @SortDefault(sort = {"id"}, direction = Sort.Direction.DESC) Pageable pageRequest,
        @RequestParam(value = "category", required = false) Project.Category category
    ) {
        log.info("/project/controller : 프로젝트 게시글 리스트 조회 request - pageable: {}, category: {}", pageRequest, category);
        return ResponseEntity.ok().body(projectService.readProjectList(pageRequest, category));
    }

    @GetMapping(READ_PROJECT)
    @Operation(summary = "프로젝트 게시글 디테일")
    public ResponseEntity<SuccessResponseDto<ProjectReadResponseDto>> readProject(
        @AuthenticationPrincipal String memberId,
        @PathVariable("projectId") Long projectId,
        HttpServletRequest request
    ) {
        log.info("/project/controller : 프로젝트 게시글 디테일");
        // todo : 조회수 증가 서비스 추가 필요(프로젝트 조회 서비스에서 분리)
        return ResponseEntity.ok().body(projectService.readProject(memberId, projectId));
    }
    
}
