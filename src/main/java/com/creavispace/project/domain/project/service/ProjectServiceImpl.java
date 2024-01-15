package com.creavispace.project.domain.project.service;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.creavispace.project.domain.project.dto.request.ProjectCreateRequestDto;
import com.creavispace.project.domain.project.dto.request.ProjectLinkDto;
import com.creavispace.project.domain.project.dto.request.ProjectMemberDto;
import com.creavispace.project.domain.project.dto.request.ProjectModifyRequestDto;
import com.creavispace.project.domain.project.dto.request.ProjectTechStackDto;
import com.creavispace.project.domain.project.dto.response.PopularProjectReadResponseDto;
import com.creavispace.project.domain.project.dto.response.ProjectCreateResponseDto;
import com.creavispace.project.domain.project.dto.response.ProjectModifyResponseDto;
import com.creavispace.project.domain.project.entity.Project;
import com.creavispace.project.domain.project.entity.ProjectMember;
import com.creavispace.project.domain.project.entity.ProjectLink;
import com.creavispace.project.domain.project.entity.ProjectTechStack;
import com.creavispace.project.domain.project.repository.ProjectMemberRepository;
import com.creavispace.project.domain.project.repository.ProjectRepository;
import com.creavispace.project.domain.project.repository.ProjectTechStackRepository;
import com.creavispace.project.domain.project.repository.ProjectLinkRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService{

    private ProjectRepository projectRepository;
    private ProjectMemberRepository projectMemberRepository;
    private ProjectTechStackRepository projectTechStackRepository;
    private ProjectLinkRepository projectLinkRepository;
    
    @Override
    @Transactional
    public ResponseEntity createProject(ProjectCreateRequestDto dto) {
        Project createProject = new Project(dto);

        projectRepository.save(createProject);

        Long projectId = createProject.getId();
        List<ProjectMember> memberList = ProjectMember.copyList(dto.getMemberList(), projectId);
        List<ProjectTechStack> techStackList = ProjectTechStack.copyList(dto.getTechStackList(), projectId);
        List<ProjectLink> linkList = ProjectLink.copyList(dto.getLinkList(), projectId);

        if(memberList != null)
            projectMemberRepository.saveAll(memberList);
        if(techStackList != null)
            projectTechStackRepository.saveAll(techStackList);
        if(linkList != null)
            projectLinkRepository.saveAll(linkList);

        ProjectCreateResponseDto create = new ProjectCreateResponseDto(createProject);

        return ResponseEntity.ok().body("프로젝트 게시글 생성이 완료되었습니다.");
    }

    @Override
    @Transactional
    public ResponseEntity modifyProject(ProjectModifyRequestDto dto) {
        // todo : JWT의 정보로 project작성자와 관리자권한에 대한 확인 로직 필요
        // long memberId = "토큰정보";
        long projectId = dto.getId();
        List<ProjectMemberDto> memberDtoList = dto.getMemberList();
        List<ProjectTechStackDto> techStackDtoList = dto.getTechStackList();
        List<ProjectLinkDto> linkDtoList = dto.getLinkList();

        Project project = projectRepository.findById(projectId).orElse(null);
        
        // if(memberId != project.getMemberId() && !member.getRole().equals("Administrator")){
        //     return ResponseEntity.status(401).body(new FailResponseDto(false,"프로젝트 게시글을 삭제할 수 있는 권한이 없습니다.", 401));
        // }
        
        project.modify(dto);
        projectRepository.save(project);

        // 삭제된 맴버
        List<Long> modifyMemberIdList = ProjectMemberDto.modifyIdList(memberDtoList);
        projectMemberRepository.deleteByNotModifyMemberIdList(projectId, modifyMemberIdList);
        // 맴버 수정 저장
        List<ProjectMember> memberList = ProjectMember.copyList(memberDtoList, projectId);
        if(memberList != null)
            projectMemberRepository.saveAll(memberList);
            
        // 삭제된 기술스택
        List<Long> modifyTechStackIdList = ProjectTechStackDto.modifyIdList(techStackDtoList);
        projectTechStackRepository.deleteByNotModifyTechStackIdList(projectId, modifyTechStackIdList);
        // 기술스택 수정 저장
        List<ProjectTechStack> techStackList = ProjectTechStack.copyList(techStackDtoList, projectId);
        if(techStackList != null)
            projectTechStackRepository.saveAll(techStackList);

        // 삭제된 링크
        List<Long> modifyLinkIdList = ProjectLinkDto.modifyIdList(linkDtoList);
        projectLinkRepository.deleteByNotModifyLinkIdList(projectId, modifyLinkIdList);
        // 링크 수정 저장
        List<ProjectLink> linkList = ProjectLink.copyList(linkDtoList, projectId);
        if(linkList != null)
            projectLinkRepository.saveAll(linkList);

        ProjectModifyResponseDto modify = new ProjectModifyResponseDto(project);

        return ResponseEntity.ok().body("프로젝트 게시글 수정이 완료되었습니다.");
    }

    /** 
     * 프로젝트 게시글을 비활성화 하는 기능
     */
    @Override
    @Transactional
    public ResponseEntity deleteProject(long projectId) {
        // todo : JWT의 정보로 project작성자와 관리자권한에 대한 확인 로직 필요
        // long memberId = "토큰정보";

        Project project = projectRepository.findById(projectId).orElse(null);
        
        // if(memberId != project.getMemberId() && !member.getRole().equals("Administrator")){
        //     return ResponseEntity.status(401).body(new FailResponseDto(false,"프로젝트 게시글을 삭제할 수 있는 권한이 없습니다.", 401));
        // }

        project.disable();
        projectRepository.save(project);

        return ResponseEntity.ok().body("프로젝트 게시글 삭제가 완료되었습니다.");
    }

    @Override
    public ResponseEntity readPopularProjectList() {

        List<Project> projectList = projectRepository.findTop5ByOrderByWeekViewCountDesc();

        List<PopularProjectReadResponseDto> popularProjectList = PopularProjectReadResponseDto.copyList(projectList);

        return ResponseEntity.ok().body("인기프로젝트 게시글 리스트 조회가 완료되었습니다.");
    }


}
