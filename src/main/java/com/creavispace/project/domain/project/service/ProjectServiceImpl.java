package com.creavispace.project.domain.project.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.creavispace.project.domain.common.dto.PostType;
import com.creavispace.project.domain.common.dto.SuccessResponseDto;
import com.creavispace.project.domain.common.entity.TechStack;
import com.creavispace.project.domain.common.repository.TechStackRepository;
import com.creavispace.project.domain.member.entity.Member;
import com.creavispace.project.domain.member.repository.MemberRepository;
import com.creavispace.project.domain.project.dto.request.ProjectRequestDto;
import com.creavispace.project.domain.project.dto.request.ProjectLinkRequestDto;
import com.creavispace.project.domain.project.dto.request.ProjectMemberRequestDto;
import com.creavispace.project.domain.project.dto.request.ProjectTechStackRequestDto;
import com.creavispace.project.domain.project.dto.response.ProjectResponseDto;
import com.creavispace.project.domain.project.dto.response.PopularProjectReadResponseDto;
import com.creavispace.project.domain.project.dto.response.ProjectDeleteResponseDto;
import com.creavispace.project.domain.project.dto.response.ProjectLinkResponseDto;
import com.creavispace.project.domain.project.dto.response.ProjectListReadResponseDto;
import com.creavispace.project.domain.project.dto.response.ProjectMemberResponseDto;
import com.creavispace.project.domain.project.dto.response.ProjectPositionResponseDto;
import com.creavispace.project.domain.project.dto.response.ProjectTechStackResponseDto;
import com.creavispace.project.domain.project.entity.Project;
import com.creavispace.project.domain.project.entity.ProjectLink;
import com.creavispace.project.domain.project.entity.ProjectMember;
import com.creavispace.project.domain.project.entity.ProjectTechStack;
import com.creavispace.project.domain.project.repository.ProjectLinkRepository;
import com.creavispace.project.domain.project.repository.ProjectMemberRepository;
import com.creavispace.project.domain.project.repository.ProjectRepository;
import com.creavispace.project.domain.project.repository.ProjectTechStackRepository;
import com.creavispace.project.global.exception.CreaviCodeException;
import com.creavispace.project.global.exception.GlobalErrorCode;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService{

    private final ProjectRepository projectRepository;
    private final MemberRepository memberRepository;
    private final TechStackRepository techStackRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final ProjectTechStackRepository projectTechStackRepository;
    private final ProjectLinkRepository projectLinkRepository;

    @Override
    @Transactional
    public SuccessResponseDto<ProjectResponseDto> createProject(Long memberId, ProjectRequestDto dto) {
        List<ProjectMemberRequestDto> memberDtos = dto.getMemberDtos();
        List<ProjectTechStackRequestDto> techStackDtos = dto.getTechStackDtos();
        List<ProjectLinkRequestDto> linkDtos = dto.getLinkDtos();

        Optional<Member> optionalMember = memberRepository.findById(memberId);
        Member member = optionalMember.orElseThrow(()-> new CreaviCodeException(GlobalErrorCode.MEMBER_NOT_FOUND));

        Project project = Project.builder()
            .member(member)
            .category(dto.getCategory())
            .title(dto.getTitle())
            .content(dto.getContent())
            .thumbnail(dto.getThumbnail())
            .bannerContent(dto.getBannerContent())
            .viewCount(0)
            .weekViewCount(0)
            .status(Boolean.TRUE)
            .build();
        
        projectRepository.save(project);

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
            
            projectMemberRepository.saveAll(projectMembers);
        }
        
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
            projectTechStackRepository.saveAll(projectTechStacks);
        }
        
        if(linkDtos != null && !linkDtos.isEmpty()){
            List<ProjectLink> projectLinks = linkDtos.stream()
                .map(techStackDto ->{
                    return ProjectLink.builder()
                        .project(project)
                        .type(techStackDto.getType())
                        .url(techStackDto.getUrl())
                        .build();
                })
                .collect(Collectors.toList());
            projectLinkRepository.saveAll(projectLinks);
        }

        Long projectId = project.getId();
        List<ProjectMember> projectPositions = projectMemberRepository.findByProjectIdOrderByPosition(projectId);
        List<ProjectLink> projectLinks = projectLinkRepository.findByProjectId(projectId);
        List<ProjectTechStack> projectTechStacks = projectTechStackRepository.findByProjectId(projectId);

        List<ProjectPositionResponseDto> positions = projectPositions.stream()
            .map(projectPosition -> {
                List<ProjectMember> projectMembers = projectMemberRepository.findByProjectIdAndPosition(projectId, projectPosition.getPosition());
                List<ProjectMemberResponseDto> projectMemberDtos = projectMembers.stream()
                    .map(projectMember -> ProjectMemberResponseDto.builder()
                        .memberId(projectMember.getMember().getId())
                        .memberProfile(projectMember.getMember().getProfileUrl())
                        .memberNickname(projectMember.getMember().getMemberNickname())
                        .build())
                    .collect(Collectors.toList());
                    
                return ProjectPositionResponseDto.builder()
                    .position(projectPosition.getPosition())
                    .members(projectMemberDtos)
                    .build();
            })
            .collect(Collectors.toList());

        List<ProjectLinkResponseDto> links = projectLinks.stream()
            .map(projectLink -> ProjectLinkResponseDto.builder()
                .type(projectLink.getType())
                .url(projectLink.getUrl())
                .build())
            .collect(Collectors.toList());

        List<ProjectTechStackResponseDto> techStacks = projectTechStacks.stream()
            .map(projectTechStack -> ProjectTechStackResponseDto.builder()
                .techStackId(projectTechStack.getTechStack().getId())
                .techStack(projectTechStack.getTechStack().getTechStack())
                .iconUrl(projectTechStack.getTechStack().getIconUrl())
                .build())
            .collect(Collectors.toList());
            
        ProjectResponseDto create = ProjectResponseDto.builder()
            .id(project.getId())
            .postType(PostType.PROJECT.getName())
            .memberId(memberId)
            .category(project.getCategory())
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

        // 성공적인 응답 반환
        return new SuccessResponseDto<>(true, "프로젝트 게시글 생성이 완료 되었습니다.", create);
    }

    @Override
    @Transactional
    public SuccessResponseDto<ProjectResponseDto> modifyProject(Long memberId, Long projectId, ProjectRequestDto dto) {
        List<ProjectMemberRequestDto> memberDtos = dto.getMemberDtos();
        List<ProjectTechStackRequestDto> techStackDtos = dto.getTechStackDtos();
        List<ProjectLinkRequestDto> linkDtos = dto.getLinkDtos();

        Optional<Member> optionalMember = memberRepository.findById(memberId);
        Member member = optionalMember.orElseThrow(()-> new CreaviCodeException(GlobalErrorCode.MEMBER_NOT_FOUND));

        Optional<Project> optionalProject = projectRepository.findById(projectId);
        Project project = optionalProject.orElseThrow(()-> new CreaviCodeException(GlobalErrorCode.PROJECT_NOT_FOUND));

        if(project.getMember().getId() != memberId && !member.getRole().equals("Administrator")){
            throw new CreaviCodeException(GlobalErrorCode.NOT_PERMISSMISSION);
        }

        Project modifyProject = project.toBuilder()
            .category(dto.getCategory())
            .title(dto.getTitle())
            .content(dto.getContent())
            .field(dto.getField())
            .thumbnail(dto.getThumbnail())
            .bannerContent(dto.getBannerContent())
            .build();

        projectRepository.save(modifyProject);

        projectMemberRepository.deleteByProjectId(projectId);

        if(memberDtos != null && !memberDtos.isEmpty()){
            List<ProjectMember> projectMembers = memberDtos.stream()
                .map(memberDto -> {
                    Member projectMember = memberRepository.findById(memberDto.getMemberId()).orElseThrow(()-> new CreaviCodeException(GlobalErrorCode.MEMBER_NOT_FOUND));
                    return ProjectMember.builder()
                        .member(projectMember)
                        .project(modifyProject)
                        .position(memberDto.getPosition())
                        .build();
                })
                .collect(Collectors.toList());
            
            projectMemberRepository.saveAll(projectMembers);
        }

        projectTechStackRepository.deleteByProjectId(projectId);

        if(techStackDtos != null && !techStackDtos.isEmpty()){
            List<ProjectTechStack> projectTechStacks = techStackDtos.stream()
                .map(techStackDto ->{
                    TechStack projectTechStack = techStackRepository.findById(techStackDto.getTechStackId()).orElseThrow(()-> new CreaviCodeException(GlobalErrorCode.TECHSTACK_NOT_FOUND));
                    return ProjectTechStack.builder()
                        .techStack(projectTechStack)
                        .project(modifyProject)
                        .build();
                })
                .collect(Collectors.toList());
            projectTechStackRepository.saveAll(projectTechStacks);
        }

        projectLinkRepository.deleteByProjectId(projectId);
        
        if(linkDtos != null && !linkDtos.isEmpty()){
            List<ProjectLink> projectLinks = linkDtos.stream()
                .map(techStackDto ->{
                    return ProjectLink.builder()
                        .project(modifyProject)
                        .type(techStackDto.getType())
                        .url(techStackDto.getUrl())
                        .build();
                })
                .collect(Collectors.toList());
            projectLinkRepository.saveAll(projectLinks);
        }

        List<ProjectMember> projectPositions = projectMemberRepository.findByProjectIdOrderByPosition(projectId);
        List<ProjectLink> projectLinks = projectLinkRepository.findByProjectId(projectId);
        List<ProjectTechStack> projectTechStacks = projectTechStackRepository.findByProjectId(projectId);

        List<ProjectPositionResponseDto> positions = projectPositions.stream()
            .map(projectPosition -> {
                List<ProjectMember> projectMembers = projectMemberRepository.findByProjectIdAndPosition(projectId, projectPosition.getPosition());
                List<ProjectMemberResponseDto> projectMemberDtos = projectMembers.stream()
                    .map(projectMember -> ProjectMemberResponseDto.builder()
                        .memberId(projectMember.getMember().getId())
                        .memberProfile(projectMember.getMember().getProfileUrl())
                        .memberNickname(projectMember.getMember().getMemberNickname())
                        .build())
                    .collect(Collectors.toList());
                    
                return ProjectPositionResponseDto.builder()
                    .position(projectPosition.getPosition())
                    .members(projectMemberDtos)
                    .build();
            })
            .collect(Collectors.toList());

        List<ProjectLinkResponseDto> links = projectLinks.stream()
            .map(projectLink -> ProjectLinkResponseDto.builder()
                .type(projectLink.getType())
                .url(projectLink.getUrl())
                .build())
            .collect(Collectors.toList());

        List<ProjectTechStackResponseDto> techStacks = projectTechStacks.stream()
            .map(projectTechStack -> ProjectTechStackResponseDto.builder()
                .techStackId(projectTechStack.getTechStack().getId())
                .techStack(projectTechStack.getTechStack().getTechStack())
                .iconUrl(projectTechStack.getTechStack().getIconUrl())
                .build())
            .collect(Collectors.toList());
            
        ProjectResponseDto modify = ProjectResponseDto.builder()
            .id(modifyProject.getId())
            .postType(PostType.PROJECT.getName())
            .memberId(memberId)
            .category(modifyProject.getCategory())
            .field(modifyProject.getField())
            .title(modifyProject.getTitle())
            .content(modifyProject.getContent())
            .thumbnail(modifyProject.getThumbnail())
            .bannerContent(modifyProject.getBannerContent())
            .viewCount(modifyProject.getViewCount())
            .createdDate(modifyProject.getCreatedDate())
            .modifiedDate(modifyProject.getModifiedDate())
            .positions(positions)
            .links(links)
            .techStacks(techStacks)
            .build();

        return new SuccessResponseDto<>(true, "프로젝트 게시글의 수정이 완료되었습니다.", modify);
    }

    @Override
    @Transactional
    public SuccessResponseDto<ProjectDeleteResponseDto> deleteProject(Long memberId, Long projectId) {
        Optional<Member> optionalMember = memberRepository.findById(memberId);
        Member member = optionalMember.orElseThrow(()-> new CreaviCodeException(GlobalErrorCode.MEMBER_NOT_FOUND));
        
        Optional<Project> optionalProject = projectRepository.findByIdAndStatusTrue(projectId);
        Project project = optionalProject.orElseThrow(()-> new CreaviCodeException(GlobalErrorCode.PROJECT_NOT_FOUND));
        
        if(project.getMember().getId() != memberId && !member.getRole().equals("Administrator")){
            throw new CreaviCodeException(GlobalErrorCode.NOT_PERMISSMISSION);
        }

        Project disableProject = project.toBuilder()
            .status(false)
            .build();

        projectRepository.save(disableProject);

        ProjectDeleteResponseDto delete = ProjectDeleteResponseDto.builder()
            .projectId(disableProject.getId())
            .postType(PostType.PROJECT.getName())
            .build();

        return new SuccessResponseDto<>(true, "프로젝트 게시글 삭제가 완료되었습니다.", delete);
    }

    @Override
    public SuccessResponseDto<List<PopularProjectReadResponseDto>> readPopularProjectList() {
        List<Project> projects = projectRepository.findTop6ByStatusTrueOrderByWeekViewCountDesc();

        List<PopularProjectReadResponseDto> readPopularList = projects.stream()
            .map(project -> PopularProjectReadResponseDto.builder()
                .id(project.getId())
                .postType(PostType.PROJECT.getName())
                .title(project.getTitle())
                .thumbnail(project.getThumbnail())
                .category(project.getCategory())
                .bannerContent(project.getBannerContent())
                .build())
            .collect(Collectors.toList());

        return new SuccessResponseDto<>(true, "인기 프로젝트 조회가 완료 되었습니다.", readPopularList);
    }

    @Override
    public SuccessResponseDto<List<ProjectListReadResponseDto>> readProjectList(Integer size, Integer page, String category) {
        Pageable pageRequest = PageRequest.of(page-1, size);
        Page<Project> pageable;

        if(category != null && !category.isEmpty()){
            pageable = projectRepository.findAllByStatusTrueAndCategory(category, pageRequest);
        }else{
            pageable = projectRepository.findAllByStatusTrue(pageRequest);
        }

        if(!pageable.hasContent()) new CreaviCodeException(GlobalErrorCode.NOT_PROJECT_CONTENT);
        List<Project> projects = pageable.getContent();

        List<ProjectListReadResponseDto> reads = projects.stream()
            .map(project -> {
                List<ProjectLinkResponseDto> links = project.getLinks().stream()
                    .map(link -> ProjectLinkResponseDto.builder()
                        .type(link.getType())
                        .url(link.getUrl())
                        .build())
                    .collect(Collectors.toList());

                return ProjectListReadResponseDto.builder()
                .id(project.getId())
                .postType(PostType.PROJECT.getName())
                .category(project.getCategory())
                .title(project.getTitle())
                .thumbnail(project.getThumbnail())
                .bannerContent(project.getBannerContent())
                .links(links)
                .build();
                })
            .collect(Collectors.toList());

        return new SuccessResponseDto<>(true, "프로젝트 게시글 리스트 조회가 완료되었습니다.", reads);
    }

    @Override
    public SuccessResponseDto<ProjectResponseDto> readProject(Long memberId, Long projectId) {
        Optional<Project> optionalProject;

        boolean isWriter = projectRepository.existsByIdAndMemberId(projectId, memberId);
        
        if(isWriter){
            optionalProject = projectRepository.findByIdAndStatusTrue(projectId);
        }else{
            optionalProject = projectRepository.findById(projectId);
        }

        Project project = optionalProject.orElseThrow(()-> new CreaviCodeException(GlobalErrorCode.PROJECT_NOT_FOUND));

        List<String> uniquePositions = project.getMembers().stream()
            .map(member -> member.getPosition())
            .distinct()
            .collect(Collectors.toList());

        List<ProjectPositionResponseDto> positions = uniquePositions.stream()
            .map(position -> ProjectPositionResponseDto.builder()
                .position(position)
                .members(project.getMembers().stream()
                    .filter(member -> member.getPosition().equals(position))
                    .map(member -> ProjectMemberResponseDto.builder()
                        .memberId(member.getMember().getId())
                        .memberProfile(member.getMember().getProfileUrl())
                        .memberNickname(member.getMember().getMemberNickname())
                        .build())
                    .collect(Collectors.toList()))
                .build())
            .collect(Collectors.toList());

        List<ProjectLinkResponseDto> links = project.getLinks().stream()
        .map(projectLink -> ProjectLinkResponseDto.builder()
            .type(projectLink.getType())
            .url(projectLink.getUrl())
            .build())
        .collect(Collectors.toList());

        List<ProjectTechStackResponseDto> techStacks = project.getTechStacks().stream()
            .map(projectTechStack -> ProjectTechStackResponseDto.builder()
                .techStackId(projectTechStack.getTechStack().getId())
                .techStack(projectTechStack.getTechStack().getTechStack())
                .iconUrl(projectTechStack.getTechStack().getIconUrl())
                .build())
            .collect(Collectors.toList());
        
        ProjectResponseDto read = ProjectResponseDto.builder()
            .id(project.getId())
            .postType(PostType.PROJECT.getName())
            .memberId(project.getMember().getId())
            .category(project.getCategory())
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

        return new SuccessResponseDto<>(true, "프로젝트 게시글 상세조회가 완료되었습니다.", read);
    }


}
