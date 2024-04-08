package com.creavispace.project.domain.project.service;

import com.creavispace.project.domain.common.dto.response.SuccessResponseDto;
import com.creavispace.project.domain.common.dto.type.PostType;
import com.creavispace.project.domain.common.dto.type.ProjectCategory;
import com.creavispace.project.domain.member.Role;
import com.creavispace.project.domain.member.entity.Member;
import com.creavispace.project.domain.member.repository.MemberRepository;
import com.creavispace.project.domain.project.dto.request.ProjectLinkRequestDto;
import com.creavispace.project.domain.project.dto.request.ProjectMemberRequestDto;
import com.creavispace.project.domain.project.dto.request.ProjectRequestDto;
import com.creavispace.project.domain.project.dto.request.ProjectTechStackRequestDto;
import com.creavispace.project.domain.project.dto.response.*;
import com.creavispace.project.domain.project.entity.Project;
import com.creavispace.project.domain.project.entity.ProjectLink;
import com.creavispace.project.domain.project.entity.ProjectMember;
import com.creavispace.project.domain.project.entity.ProjectTechStack;
import com.creavispace.project.domain.project.repository.ProjectLinkRepository;
import com.creavispace.project.domain.project.repository.ProjectMemberRepository;
import com.creavispace.project.domain.project.repository.ProjectRepository;
import com.creavispace.project.domain.project.repository.ProjectTechStackRepository;
import com.creavispace.project.domain.techStack.entity.TechStack;
import com.creavispace.project.domain.techStack.repository.TechStackRepository;
import com.creavispace.project.global.exception.CreaviCodeException;
import com.creavispace.project.global.exception.GlobalErrorCode;
import com.creavispace.project.global.util.CustomValueOf;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final MemberRepository memberRepository;
    private final TechStackRepository techStackRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final ProjectTechStackRepository projectTechStackRepository;
    private final ProjectLinkRepository projectLinkRepository;

    @Override
    @Transactional
    public SuccessResponseDto<ProjectResponseDto> createProject(String memberId, ProjectRequestDto dto) {
        ProjectResponseDto data = null;
        List<ProjectMemberRequestDto> memberDtos = dto.getMemberDtos();
        List<ProjectTechStackRequestDto> techStackDtos = dto.getTechStackDtos();
        List<ProjectLinkRequestDto> linkDtos = dto.getLinkDtos();
        ProjectCategory categoryEnum = CustomValueOf.valueOf(ProjectCategory.class, dto.getCategory(), GlobalErrorCode.NOT_FOUND_PROJECT_CATEGORY);

        // JWT에 저장된 회원이 존재하는지
        Optional<Member> optionalMember = memberRepository.findById(memberId);
        Member member = optionalMember.orElseThrow(()-> new CreaviCodeException(GlobalErrorCode.MEMBER_NOT_FOUND));

        // 프로젝트 생성
        Project project = Project.builder()
                .member(member)
                .category(categoryEnum)
                .title(dto.getTitle())
                .content(dto.getContent())
                .thumbnail(dto.getThumbnail())
                .bannerContent(dto.getBannerContent())
                .viewCount(0)
                .weekViewCount(0)
                .status(Boolean.TRUE)
                .build();

        // 프로젝트 저장
        projectRepository.save(project);

        // 프로젝트 맴버가 있다면
        if(memberDtos != null && !memberDtos.isEmpty()){
            List<ProjectMember> projectMembers = memberDtos.stream()
                    .map(memberDto -> {
                        Member projectMember = memberRepository.findById(memberDto.getMemberId()).orElseThrow(()-> new CreaviCodeException(GlobalErrorCode.MEMBER_NOT_FOUND));
                        return ProjectMember.builder()
                                .member(projectMember)
                                .project(project)
                                .position(memberDto.getPosition())
                                .build();
                    })
                    .collect(Collectors.toList());

            // 프로젝트 맴버 저장
            projectMemberRepository.saveAll(projectMembers);
        }

        // 프로젝트 기술스택이 있다면
        if(techStackDtos != null && !techStackDtos.isEmpty()){
            List<ProjectTechStack> projectTechStacks = techStackDtos.stream()
                    .map(techStackDto ->{
                        TechStack projectTechStack = techStackRepository.findById(techStackDto.getTechStackId()).orElseThrow(()-> new CreaviCodeException(GlobalErrorCode.TECHSTACK_NOT_FOUND));
                        return ProjectTechStack.builder()
                                .techStack(projectTechStack)
                                .project(project)
                                .build();
                    })
                    .collect(Collectors.toList());

            // 프로젝트 기술스택 저장
            projectTechStackRepository.saveAll(projectTechStacks);
        }

        // 프로젝트 링크가 있다면
        if(linkDtos != null && !linkDtos.isEmpty()){
            List<ProjectLink> projectLinks = linkDtos.stream()
                    .map(techStackDto ->{
                        return ProjectLink.builder()
                                .project(project)
                                .linkType(techStackDto.getLinkType())
                                .url(techStackDto.getUrl())
                                .build();
                    })
                    .collect(Collectors.toList());
            // 프로젝트 링크 저장
            projectLinkRepository.saveAll(projectLinks);
        }

        // 저장된 맴버, 기술스택, 링크 찾아서 DTO로
        Long projectId = project.getId();
        List<ProjectMember> projectMembers = projectMemberRepository.findByProjectIdOrderByPosition(projectId);
        List<ProjectLink> projectLinks = projectLinkRepository.findByProjectId(projectId);
        List<ProjectTechStack> projectTechStacks = projectTechStackRepository.findByProjectId(projectId);

        // position을 기준으로 그룹화
        Map<String, List<Member>> positionMap = new HashMap<>();
        for(ProjectMember projectMember : projectMembers){
            positionMap.computeIfAbsent(projectMember.getPosition(), k -> new ArrayList<>()).add(projectMember.getMember());
        }

        // position으로 그룹화된 맴버 결과를 DTO로 변환
        List<ProjectPositionResponseDto> positions = positionMap.entrySet().stream()
                .map(entry -> ProjectPositionResponseDto.builder()
                        .position(entry.getKey())
                        .members(entry.getValue().stream()
                                .map(value -> ProjectMemberResponseDto.builder()
                                        .memberId(value.getId())
                                        .memberNickname(value.getMemberNickname())
                                        .memberProfile(value.getProfileUrl())
                                        .build())
                                .collect(Collectors.toList()))
                        .build())
                .collect(Collectors.toList());

        // 링크 결과를 DTO로 변환
        List<ProjectLinkResponseDto> links = projectLinks.stream()
                .map(projectLink -> ProjectLinkResponseDto.builder()
                        .linkType(projectLink.getLinkType())
                        .url(projectLink.getUrl())
                        .build())
                .collect(Collectors.toList());

        // 기술스택 결과를 DTO로 변환
        List<ProjectTechStackResponseDto> techStacks = projectTechStacks.stream()
                .map(projectTechStack -> ProjectTechStackResponseDto.builder()
                        .techStackId(projectTechStack.getTechStack().getId())
                        .techStack(projectTechStack.getTechStack().getTechStack())
                        .iconUrl(projectTechStack.getTechStack().getIconUrl())
                        .build())
                .collect(Collectors.toList());

        // 프로젝트 생성 결과 DTO
        data = ProjectResponseDto.builder()
                .id(project.getId())
                .postType(PostType.PROJECT.name())
                .memberId(project.getMember().getId())
                .memberNickName(project.getMember().getMemberNickname())
                .memberProfile(project.getMember().getProfileUrl())
                .category(project.getCategory().name())
                .field(project.getField())
                .title(project.getTitle())
                .content(project.getContent())
                .thumbnail(project.getThumbnail())
                .bannerContent(project.getBannerContent())
                .viewCount(project.getViewCount())
                .createdDate(project.getCreatedDate())
                .modifiedDate(project.getModifiedDate())
                .positions(positions)
                .links(links)
                .techStacks(techStacks)
                .build();

        log.info("/project/service : createProject success data = {}", data);
        // 성공적인 응답 반환
        return new SuccessResponseDto<>(true, "프로젝트 게시글 생성이 완료 되었습니다.", data);
    }

    @Override
    @Transactional
    public SuccessResponseDto<ProjectResponseDto> modifyProject(String memberId, Long projectId, ProjectRequestDto dto) {
        ProjectResponseDto data = null;
        List<ProjectMemberRequestDto> memberDtos = dto.getMemberDtos();
        List<ProjectTechStackRequestDto> techStackDtos = dto.getTechStackDtos();
        List<ProjectLinkRequestDto> linkDtos = dto.getLinkDtos();

        // JWT에 저장된 회원이 존재하는지
        Optional<Member> optionalMember = memberRepository.findById(memberId);
        Member member = optionalMember.orElseThrow(()-> new CreaviCodeException(GlobalErrorCode.MEMBER_NOT_FOUND));

        // 수정할 프로젝트 게시글이 존재하는지
        Optional<Project> optionalProject = projectRepository.findById(projectId);
        Project project = optionalProject.orElseThrow(()-> new CreaviCodeException(GlobalErrorCode.PROJECT_NOT_FOUND));

        // 수정할 권한이 있는지
        if(memberId.equals(project.getMember().getId()) && !member.getRole().equals(Role.ADMIN)){
            throw new CreaviCodeException(GlobalErrorCode.NOT_PERMISSMISSION);
        }

        // 프로젝트 수정 및 저장
        project.modify(dto);
        projectRepository.save(project);

        // 기존 프로젝트 맴버 삭제
        projectMemberRepository.deleteByProjectId(projectId);

        // DTO에 프로젝트 맴버가 존재하는지
        if(memberDtos != null && !memberDtos.isEmpty()){
            List<ProjectMember> projectMembers = memberDtos.stream()
                    .map(memberDto -> {
                        Member projectMember = memberRepository.findById(memberDto.getMemberId()).orElseThrow(()-> new CreaviCodeException(GlobalErrorCode.MEMBER_NOT_FOUND));
                        return ProjectMember.builder()
                                .member(projectMember)
                                .project(project)
                                .position(memberDto.getPosition())
                                .build();
                    })
                    .collect(Collectors.toList());
            // 프로젝트 맴버 저장
            projectMemberRepository.saveAll(projectMembers);
        }

        // 기존 기술스택 삭제
        projectTechStackRepository.deleteByProjectId(projectId);

        // DTO에 기술스택이 존재하는지
        if(techStackDtos != null && !techStackDtos.isEmpty()){
            List<ProjectTechStack> projectTechStacks = techStackDtos.stream()
                    .map(techStackDto ->{
                        TechStack projectTechStack = techStackRepository.findById(techStackDto.getTechStackId()).orElseThrow(()-> new CreaviCodeException(GlobalErrorCode.TECHSTACK_NOT_FOUND));
                        return ProjectTechStack.builder()
                                .techStack(projectTechStack)
                                .project(project)
                                .build();
                    })
                    .collect(Collectors.toList());
            // 기술스택 저장
            projectTechStackRepository.saveAll(projectTechStacks);
        }

        // 기존 링크 삭제
        projectLinkRepository.deleteByProjectId(projectId);

        // DTO에 링크가 존재하는지
        if(linkDtos != null && !linkDtos.isEmpty()){
            List<ProjectLink> projectLinks = linkDtos.stream()
                    .map(techStackDto ->{
                        return ProjectLink.builder()
                                .project(project)
                                .linkType(techStackDto.getLinkType())
                                .url(techStackDto.getUrl())
                                .build();
                    })
                    .collect(Collectors.toList());
            // 링크 저장
            projectLinkRepository.saveAll(projectLinks);
        }

        // 저장된 맴버, 링크, 기술스택을 찾아서 DTO로 변환
        List<ProjectMember> projectMembers = projectMemberRepository.findByProjectIdOrderByPosition(projectId);
        List<ProjectLink> projectLinks = projectLinkRepository.findByProjectId(projectId);
        List<ProjectTechStack> projectTechStacks = projectTechStackRepository.findByProjectId(projectId);

        // position을 기준으로 그룹화
        Map<String, List<Member>> positionMap = new HashMap<>();
        for(ProjectMember projectMember : projectMembers){
            positionMap.computeIfAbsent(projectMember.getPosition(), k -> new ArrayList<>()).add(projectMember.getMember());
        }

        // position으로 그룹화된 맴버 결과를 DTO로 변환
        List<ProjectPositionResponseDto> positions = positionMap.entrySet().stream()
                .map(entry -> ProjectPositionResponseDto.builder()
                        .position(entry.getKey())
                        .members(entry.getValue().stream()
                                .map(value -> ProjectMemberResponseDto.builder()
                                        .memberId(value.getId())
                                        .memberNickname(value.getMemberNickname())
                                        .memberProfile(value.getProfileUrl())
                                        .build())
                                .collect(Collectors.toList()))
                        .build())
                .collect(Collectors.toList());

        // 링크 결과를 DTO로 변환
        List<ProjectLinkResponseDto> links = projectLinks.stream()
                .map(projectLink -> ProjectLinkResponseDto.builder()
                        .linkType(projectLink.getLinkType())
                        .url(projectLink.getUrl())
                        .build())
                .collect(Collectors.toList());

        // 기술스택 결과를 DTO로 변환
        List<ProjectTechStackResponseDto> techStacks = projectTechStacks.stream()
                .map(projectTechStack -> ProjectTechStackResponseDto.builder()
                        .techStackId(projectTechStack.getTechStack().getId())
                        .techStack(projectTechStack.getTechStack().getTechStack())
                        .iconUrl(projectTechStack.getTechStack().getIconUrl())
                        .build())
                .collect(Collectors.toList());

        // 프로젝트 수정 결과 DTO
        data = ProjectResponseDto.builder()
                .id(project.getId())
                .postType(PostType.PROJECT.name())
                .memberId(project.getMember().getId())
                .memberNickName(project.getMember().getMemberNickname())
                .memberProfile(project.getMember().getProfileUrl())
                .category(project.getCategory().name())
                .field(project.getField())
                .title(project.getTitle())
                .content(project.getContent())
                .thumbnail(project.getThumbnail())
                .bannerContent(project.getBannerContent())
                .viewCount(project.getViewCount())
                .createdDate(project.getCreatedDate())
                .modifiedDate(project.getModifiedDate())
                .positions(positions)
                .links(links)
                .techStacks(techStacks)
                .build();

        log.info("/project/service : modifyProject success data = {}", data);
        // 성공 응답 반환
        return new SuccessResponseDto<>(true, "프로젝트 게시글의 수정이 완료되었습니다.", data);
    }

    @Override
    @Transactional
    public SuccessResponseDto<ProjectDeleteResponseDto> deleteProject(String memberId, Long projectId) {
        ProjectDeleteResponseDto data = null;
        // JWT에 저장된 회원이 존재하는지
        Optional<Member> optionalMember = memberRepository.findById(memberId);
        Member member = optionalMember.orElseThrow(()-> new CreaviCodeException(GlobalErrorCode.MEMBER_NOT_FOUND));

        // 삭제할 프로젝트가 존재하는지
        Optional<Project> optionalProject = projectRepository.findByIdAndStatusTrue(projectId);
        Project project = optionalProject.orElseThrow(()-> new CreaviCodeException(GlobalErrorCode.PROJECT_NOT_FOUND));

        // 삭제할 권한이 있는지
        if(memberId.equals(project.getMember().getId()) && !member.getRole().equals(Role.ADMIN)){
            throw new CreaviCodeException(GlobalErrorCode.NOT_PERMISSMISSION);
        }

        // 비활성화 변경 및 저장
        project.disable();
        projectRepository.save(project);

        // 프로젝트 삭제 결과 DTO
        data = ProjectDeleteResponseDto.builder()
                .projectId(project.getId())
                .postType(PostType.PROJECT.name())
                .build();

        log.info("/project/service : deleteProject success data = {}", data);
        // 성공 응답 반환
        return new SuccessResponseDto<>(true, "프로젝트 게시글 삭제가 완료되었습니다.", data);
    }

    @Override
    public SuccessResponseDto<List<PopularProjectReadResponseDto>> readPopularProjectList() {
        List<PopularProjectReadResponseDto> data = null;
        // 인기 프로젝트 가져옴(주간 조회수 기준 Top 6개)
        List<Project> projects = projectRepository.findTop6ByStatusTrueOrderByWeekViewCountDesc();

        // 인기 프로젝트 결과 DTO
        data = projects.stream()
                .map(project -> PopularProjectReadResponseDto.builder()
                        .id(project.getId())
                        .postType(PostType.PROJECT.name())
                        .title(project.getTitle())
                        .thumbnail(project.getThumbnail())
                        .category(project.getCategory().name())
                        .bannerContent(project.getBannerContent())
                        .build())
                .collect(Collectors.toList());

        log.info("/project/service : readPopularProjectList success data = {}", data);
        // 성공 응답 반환
        return new SuccessResponseDto<>(true, "인기 프로젝트 조회가 완료 되었습니다.", data);
    }

    @Override
    public SuccessResponseDto<List<ProjectListReadResponseDto>> readProjectList(Integer size, Integer page, ProjectCategory category) {
        List<ProjectListReadResponseDto> data = null;
        Pageable pageRequest = PageRequest.of(page-1, size);
        Page<Project> pageable;

        System.out.println(category);

        // 카테고리가 존재하면
        if(category != null){
            pageable = projectRepository.findAllByStatusTrueAndCategory(category.name(), pageRequest);
        }
        // 카테고리가 없다면
        else{
            pageable = projectRepository.findAllByStatusTrue(pageRequest);
        }

        // 해당 페이지에 게시글이 있는지
        if(!pageable.hasContent()) throw new CreaviCodeException(GlobalErrorCode.NOT_PROJECT_CONTENT);
        List<Project> projects = pageable.getContent();

        // 프로젝트 리스트 결과 DTO
        data = getProjectListReadResponseDtos(projects);

        log.info("/project/service : readProjectList success data = {}", data);
        // 성공 응답 반환
        return new SuccessResponseDto<>(true, "프로젝트 게시글 리스트 조회가 완료되었습니다.", data);
    }

    @Override
    @Transactional
    public SuccessResponseDto<ProjectReadResponseDto> readProject(String memberId, Long projectId, HttpServletRequest request) {
        ProjectReadResponseDto data = null;
        Optional<Project> optionalProject;

        // JWT 회원과 프로젝트 게시글 작성자와 일치하는지
        boolean isWriter = projectRepository.existsByIdAndMemberId(projectId, memberId);
        // 일치하면 비활성화 프로젝트도 조회가능
        if(isWriter){
            optionalProject = projectRepository.findById(projectId);
        }
        // 일치하지 않으면 활성화된 프로젝트만 조회가능
        else{
            optionalProject = projectRepository.findByIdAndStatusTrue(projectId);
        }

        Project project = optionalProject.orElseThrow(()-> new CreaviCodeException(GlobalErrorCode.PROJECT_NOT_FOUND));

        // 조회수 증가
        String projectViewLogHeader = request.getHeader("projectViewLog");
        // 해더에 projectView값이 없으면
        if(projectViewLogHeader == null){
			// 조회수 +1
			project.plusViewCount();
			projectRepository.save(project);
			projectViewLogHeader = "["+ projectId + "]";
        }
		// 헤더에 projectView값이 있으면
		else{
			// 조회한적이 없는 프로젝트일 경우
			if(!projectViewLogHeader.contains("[" + projectId + "]")){
				// 조회수 +1
				project.plusViewCount();
				projectRepository.save(project);
				projectViewLogHeader += "_[" + projectId + "]";
			}
        }

        // 프로젝트 맴버를 position을 기준으로 그룹화
        Map<String, List<Member>> positionMap = new HashMap<>();
        for(ProjectMember projectMember : project.getMembers()){
            positionMap.computeIfAbsent(projectMember.getPosition(), k -> new ArrayList<>()).add(projectMember.getMember());
        }

        // 그룹화한 맴버를 DTO로 변환
        List<ProjectPositionResponseDto> positions = positionMap.entrySet().stream()
                .map(entry -> ProjectPositionResponseDto.builder()
                        .position(entry.getKey())
                        .members(entry.getValue().stream()
                                .map(value -> ProjectMemberResponseDto.builder()
                                        .memberId(value.getId())
                                        .memberNickname(value.getMemberNickname())
                                        .memberProfile(value.getProfileUrl())
                                        .build())
                                .collect(Collectors.toList()))
                        .build())
                .collect(Collectors.toList());

        // 프로젝트의 링크를 DTO로 변환
        List<ProjectLinkResponseDto> links = project.getLinks().stream()
                .map(projectLink -> ProjectLinkResponseDto.builder()
                        .linkType(projectLink.getLinkType())
                        .url(projectLink.getUrl())
                        .build())
                .collect(Collectors.toList());

        // 프로젝트의 기술스택을 DTO로 변환
        List<ProjectTechStackResponseDto> techStacks = project.getTechStacks().stream()
                .map(projectTechStack -> ProjectTechStackResponseDto.builder()
                        .techStackId(projectTechStack.getTechStack().getId())
                        .techStack(projectTechStack.getTechStack().getTechStack())
                        .iconUrl(projectTechStack.getTechStack().getIconUrl())
                        .build())
                .collect(Collectors.toList());

        // 프로젝트 디테일 결과 DTO
        data = ProjectReadResponseDto.builder()
                .id(project.getId())
                .postType(PostType.PROJECT.name())
                .memberId(project.getMember().getId())
                .memberNickName(project.getMember().getMemberNickname())
                .memberProfile(project.getMember().getProfileUrl())
                .category(project.getCategory().name())
                .field(project.getField())
                .title(project.getTitle())
                .content(project.getContent())
                .thumbnail(project.getThumbnail())
                .bannerContent(project.getBannerContent())
                .viewCount(project.getViewCount())
                .createdDate(project.getCreatedDate())
                .modifiedDate(project.getModifiedDate())
                .positions(positions)
                .links(links)
                .techStacks(techStacks)
                .projectViewLog(projectViewLogHeader)
                .build();

        log.info("/project/service : readProject success data = {}", data);
        // 성공 응답 반환
        return new SuccessResponseDto<>(true, "프로젝트 게시글 상세조회가 완료되었습니다.", data);
    }

    //ky

    @Override
    public SuccessResponseDto<List<ProjectListReadResponseDto>> readMyProjectList(Member member, Integer size,
                                                                                  Integer page, String sortType) {
        Pageable pageRequest = PageRequest.of(page - 1, size);
        Page<Project> pageable = getProjects(member.getId(), sortType, pageRequest);



        if (!pageable.hasContent()) {
            throw new CreaviCodeException(GlobalErrorCode.NOT_PROJECT_CONTENT);
        }
        List<Project> projects = pageable.getContent();

        List<ProjectListReadResponseDto> reads = getProjectListReadResponseDtos(projects);

        return new SuccessResponseDto<>(true, "프로젝트 게시글 리스트 조회가 완료되었습니다.", reads);
    }
    @Override
    public SuccessResponseDto<List<ProjectListReadResponseDto>> readMyProjectList(String memberId, Integer size,
                                                                                  Integer page, String sortType) {
        Pageable pageRequest = PageRequest.of(page - 1, size);
        Page<Project> pageable = getProjects(memberId, sortType, pageRequest);

        if (!pageable.hasContent()) {
            throw new CreaviCodeException(GlobalErrorCode.NOT_PROJECT_CONTENT);
        }
        List<Project> projects = pageable.getContent();

        List<ProjectListReadResponseDto> reads = getProjectListReadResponseDtos(projects);

        return new SuccessResponseDto<>(true, "프로젝트 게시글 리스트 조회가 완료되었습니다.", reads);
    }

    @Override
    public SuccessResponseDto<List<ProjectListReadResponseDto>> readMyProjectFeedBackList(String memberId, Integer size,
                                                                                          Integer page,
                                                                                          String sortType) {
        Pageable pageRequest = PageRequest.of(page - 1, size);
        Page<Project> pageable = getProjects(memberId, sortType, pageRequest);

        if (!pageable.hasContent()) {
            throw new CreaviCodeException(GlobalErrorCode.NOT_PROJECT_CONTENT);
        }
        List<Project> projects = pageable.getContent().stream().filter(Project::isFeedback).collect(
                Collectors.toList());

        List<ProjectListReadResponseDto> reads = getProjectListReadResponseDtos(projects);

        return new SuccessResponseDto<>(true, "프로젝트 게시글 리스트 조회가 완료되었습니다.", reads);
    }

    private Page<Project> getProjects(String memberId, String sortType, Pageable pageRequest) {
        Page<Project> pageable;
        if (sortType.equalsIgnoreCase("asc")) {
            pageable = projectRepository.findAllByStatusTrueAndMemberIdOrderByCreatedDateAsc(pageRequest, memberId);
            return pageable;
        }
        if (sortType.equalsIgnoreCase("desc")) {
            pageable = projectRepository.findAllByStatusTrueAndMemberIdOrderByCreatedDateDesc(pageRequest, memberId);
            return pageable;
        }
        return projectRepository.findAllByStatusTrueAndMemberIdOrderByCreatedDateAsc(pageRequest, memberId);
    }

    private static List<ProjectListReadResponseDto> getProjectListReadResponseDtos(List<Project> projects) {
        return projects.stream()
                .map(project -> {
                    List<ProjectLinkResponseDto> links = project.getLinks().stream()
                            .map(link -> ProjectLinkResponseDto.builder()
                                    .linkType(link.getLinkType())
                                    .url(link.getUrl())
                                    .build())
                            .collect(Collectors.toList());

                    return ProjectListReadResponseDto.builder()
                            .id(project.getId())
                            .postType(PostType.PROJECT.name())
                            .category(project.getCategory().name())
                            .title(project.getTitle())
                            .thumbnail(project.getThumbnail())
                            .bannerContent(project.getBannerContent())
                            .links(links)
                            .build();
                })
                .collect(Collectors.toList());
    }
}