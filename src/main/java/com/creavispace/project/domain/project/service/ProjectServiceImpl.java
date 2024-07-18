package com.creavispace.project.domain.project.service;

import com.creavispace.project.common.dto.response.SuccessResponseDto;
import com.creavispace.project.common.dto.type.PostType;
import com.creavispace.project.common.exception.CreaviCodeException;
import com.creavispace.project.common.exception.GlobalErrorCode;
import com.creavispace.project.common.post.entity.Post;
import com.creavispace.project.domain.file.entity.Image;
import com.creavispace.project.domain.file.entity.ProjectImage;
import com.creavispace.project.domain.file.service.ImageManager;
import com.creavispace.project.domain.member.entity.Member;
import com.creavispace.project.domain.member.entity.Role;
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
import com.creavispace.project.domain.project.repository.ProjectRepository;
import com.creavispace.project.domain.techStack.entity.TechStack;
import com.creavispace.project.domain.techStack.repository.TechStackRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
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
    private final ImageManager imageManager;

    @Override
    @Transactional
    public SuccessResponseDto<Long> createProject(String memberId, ProjectRequestDto dto) {
        // 맴버 엔티티 조회
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new NoSuchElementException("로그인 회원 아이디가 존재하지 않습니다."));

        // 프로젝트 맴버 생성
        List<ProjectMember> projectMembers = dto.getMemberDtos().stream()
                .map(projectMemberDto -> {
                    Member projectMember = memberRepository.findById(projectMemberDto.getMemberId()).orElseThrow(() -> new NoSuchElementException("프로젝트 맴버 아이디("+memberId+")가 존재하지 않습니다."));

                    return ProjectMember.builder()
                            .member(projectMember).position(projectMemberDto.getPosition()).build();
                })
                .toList();

        // 프로젝트 링크 생성
        List<ProjectLink> projectLinks = dto.getLinkDtos().stream()
                .map(projectLinkDto -> ProjectLink.builder()
                        .linkType(projectLinkDto.getLinkType())
                        .url(projectLinkDto.getUrl())
                        .build())
                .toList();

        // 프로젝트 기술스택 조회
        List<TechStack> techStacks = techStackRepository.findByTechStackIn(dto.getTechStackDtos().stream().map(ProjectTechStackRequestDto::getTechStack).toList());
        // 프로젝트 기술스택 생성
        List<ProjectTechStack> projectTechStacks = techStacks.stream()
                .map(projectTechStackDto -> ProjectTechStack.builder()
                        .techStack(projectTechStackDto)
                        .build())
                .toList();

        // todo : 시간이 지나면 이미지 필터를 사용해야하는걸 모를것같은데 좀더 괜찮은 방법이 있는지 확인
        // 프로젝트 이미지 필터(게시글에 사용되지 않는 이미지 필터)
        List<String> filteredImages = imageManager.deleteUnusedImagesAndFilterUsedImages(dto.getImages(), dto.getContent());
        // 프로젝트 이미지 생성
        List<ProjectImage> projectImages = filteredImages.stream()
                .map(ProjectImage::new)
                .toList();

        // 프로젝트 생성
        Project project = Project.createProject(dto, member, projectMembers, projectLinks, projectTechStacks, projectImages);

        // 프로젝트 저장
        projectRepository.save(project);

        // 성공적인 응답 반환
        return new SuccessResponseDto<>(true, "프로젝트 게시글 생성이 완료 되었습니다.", project.getId());
    }

    @Override
    @Transactional
    public SuccessResponseDto<Long> modifyProject(String memberId, Long projectId, ProjectRequestDto dto) {
        // 프로젝트 엔티티 조회
        Project project = projectRepository.findById(projectId).orElseThrow(() -> new NoSuchElementException("프로젝트 id(" + projectId + ")가 존재하지 않습니다."));

        // 수정 권한 조회
        if(!project.getMember().getId().equals(memberId))
            throw new CreaviCodeException(GlobalErrorCode.NOT_PERMISSMISSION);

        // 프로젝트 업데이트
        project.update(dto);

        // 프로젝트 맴버 업데이트
        updateProjectMembers(project, dto.getMemberDtos());

        // 프로젝트 링크 업데이트
        updateProjectLinks(project, dto.getLinkDtos());

        // 프로젝트 기술스택 업데이트
        updateProjectTechStacks(project, dto.getTechStackDtos());

        // 프로젝트 이미지 필터(게시글에 사용되지 않는 이미지 삭제)
        List<String> filteredNewImages = imageManager.deleteUnusedImagesAndFilterUsedImages(dto.getImages(), dto.getContent());
        // 프로젝트 이미지 업데이트
        updateProjectImages(project, filteredNewImages);

        // 성공 응답 반환
        return new SuccessResponseDto<>(true, "프로젝트 게시글의 수정이 완료되었습니다.", projectId);
    }

    private void updateProjectImages(Project project, List<String> newImages) {
        // 기존 프로젝트 이미지 삭제
        project.getProjectImages().clear();

        // new 프로젝트 이미지 생성
        List<ProjectImage> projectImages = newImages.stream()
                .map(ProjectImage::new)
                .toList();

        // new 프로젝트 이미지 저장
        for(ProjectImage projectImage : projectImages){
            project.addProjectImage(projectImage);
        }
    }

    private void updateProjectTechStacks(Project project, List<ProjectTechStackRequestDto> techStackDtos) {
        // 기존 프로젝트 기술스택 삭제
        project.getProjectTechStacks().clear();

        // new 프로젝트 기술스택 조회
        List<String> techStackIds = techStackDtos.stream()
                .map(ProjectTechStackRequestDto::getTechStack)
                .collect(Collectors.toList());
        List<TechStack> projectTechStacks = techStackRepository.findAllById(techStackIds);

        // new 프로젝트 기술스택 생성
        List<ProjectTechStack> newProjectTechStacks = projectTechStacks.stream()
                .map(projectTechStack -> ProjectTechStack.builder()
                        .techStack(projectTechStack)
                        .build())
                .toList();

        // new 프로젝트 기술스택 저장
        for(ProjectTechStack newTechStack : newProjectTechStacks){
            project.addProjectTechStack(newTechStack);
        }
    }

    private void updateProjectLinks(Project project, List<ProjectLinkRequestDto> linkDtos) {
        // 기존 프로젝트 링크 삭제
        project.getProjectLinks().clear();

        // new 프로젝트 링크 생성
        List<ProjectLink> newProjectLinks = linkDtos.stream()
                .map(newLink -> ProjectLink.builder()
                        .linkType(newLink.getLinkType())
                        .url(newLink.getUrl())
                        .build())
                .toList();

        // new 프로젝트 링크 저장
        for(ProjectLink newLink : newProjectLinks){
            project.addProjectLink(newLink);
        }
    }

    private void updateProjectMembers(Project project, List<ProjectMemberRequestDto> newMembers) {
        // 기존 프로젝트 맴버 삭제
        project.getProjectMembers().clear();

        // new 프로젝트 맴버 조회
        List<String> memberIds = newMembers.stream()
                .map(ProjectMemberRequestDto::getMemberId)
                .toList();

        // todo : 일치하는 데이터가 없거나 중복된 id가 존재하는 경우 이 로직은 잘못된 로직으로 수정이 필요.
        List<Member> members = memberRepository.findAllById(memberIds);

        for (int i = 0; i < newMembers.size(); i++) {
            ProjectMemberRequestDto projectMemberRequestDto = newMembers.get(i);
            Member member = members.get(i);
            // new 프로젝트 맴버 생성
            ProjectMember projectMember = ProjectMember.builder()
                    .member(member)
                    .position(projectMemberRequestDto.getPosition())
                    .build();
            // new 프로젝트 맴버 저장
            project.addProjectMember(projectMember);
        }
    }

    @Override
    @Transactional
    public SuccessResponseDto<Long> deleteProject(String memberId, Long projectId) {
        // 맴버 엔티티 조회
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new NoSuchElementException("로그인 회원 아이디가 존재하지 않습니다."));

        // 프로젝트 엔티티 조회
        Project project = projectRepository.findById(projectId)
                .orElseThrow(()-> new NoSuchElementException("프로젝트 id("+projectId+")가 존재하지 않습니다."));

        // 삭제 권한 조회
        if(!memberId.equals(project.getMember().getId()) && !member.getRole().equals(Role.ADMIN))
            throw new CreaviCodeException(GlobalErrorCode.NOT_PERMISSMISSION);

        // 프로젝트 상태 변경(DELETE)
        project.changeStatus(Post.Status.DELETE);

        // 성공 응답 반환
        return new SuccessResponseDto<>(true, "프로젝트 게시글 삭제가 완료되었습니다.", project.getId());
    }

    @Override
    public SuccessResponseDto<List<PopularProjectReadResponseDto>> readPopularProjectList() {
        List<PopularProjectReadResponseDto> data = null;
        // 인기 프로젝트 가져옴(주간 조회수 기준 Top 6개)
        List<Project> projects = projectRepository.findTop6ByStatusOrderByWeekViewCountDesc(Post.Status.PUBLIC);

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

        // 성공 응답 반환
        return new SuccessResponseDto<>(true, "인기 프로젝트 조회가 완료 되었습니다.", data);
    }

    @Override
    public SuccessResponseDto<List<ProjectListReadResponseDto>> readProjectList(Pageable pageRequest, Project.Category category) {
        List<ProjectListReadResponseDto> data = null;

        // 프로젝트 엔티티 조회
        Page<Project> pageable = projectRepository.findByStatusAndCategory(Post.Status.PUBLIC, category, pageRequest);

        // 결과값이 없으면 204 반환
        if(!pageable.hasContent()) throw new CreaviCodeException(GlobalErrorCode.NOT_PROJECT_CONTENT);

        // 프로젝트 리스트 결과 toDto
        data = pageable.getContent().stream()
                        .map(project -> ProjectListReadResponseDto.builder()
                                .id(project.getId())
                                .postType(PostType.PROJECT.name())
                                .category(project.getCategory().name())
                                .title(project.getTitle())
                                .links(project.getProjectLinks().stream().map(pl -> ProjectLinkResponseDto.builder().linkType(pl.getLinkType()).url(pl.getUrl()).build()).toList())
                                .thumbnail(project.getThumbnail())
                                .bannerContent(project.getBannerContent())
                                .status(project.getStatus().name())
                                .createdDate(project.getCreatedDate())
                                .memberNickname(project.getMember().getMemberNickname())
                                .memberProfile(project.getMember().getProfileUrl())
                                .build())
                                .toList();

        // 성공 응답 반환
        return new SuccessResponseDto<>(true, "프로젝트 게시글 리스트 조회가 완료되었습니다.", data);
    }

    @Override
    @Transactional
    public SuccessResponseDto<ProjectReadResponseDto> readProject(String memberId, Long projectId) {
        ProjectReadResponseDto data = null;

        // 맴버 엔티티 조회
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new NoSuchElementException("로그인 회원 아이디가 존재하지 않습니다."));

        // 프로젝트 엔티티 조회
        Project project = projectRepository.findById(projectId).orElseThrow(() -> new NoSuchElementException("프로젝트 id("+projectId+")가 존재하지 않습니다."));

        // 권한 조회
        if(!project.getStatus().equals(Post.Status.PUBLIC) && !project.getMember().getId().equals(memberId) && !member.getRole().equals(Role.ADMIN)){
            throw new CreaviCodeException(GlobalErrorCode.NOT_PERMISSMISSION);
        }

        // 프로젝트 엔티티 toDto
        data = toProjectReadResponseDto(project);

        // 성공 응답 반환
        return new SuccessResponseDto<>(true, "프로젝트 게시글 상세조회가 완료되었습니다.", data);
    }

    @Override
    public SuccessResponseDto<List<ProjectListReadResponseDto>> readProjectListByMemberId(Pageable pageRequest, Project.Category category, String memberId, String memberId_param) {
        List<ProjectListReadResponseDto> data = null;

        // 프로젝트 엔티티 조회 결과를 담을 객체 생성
        Page<Project> pageable;

        // 맴버 엔티티 조회
        memberRepository.findById(memberId_param).orElseThrow(() -> new NoSuchElementException("회원 id("+memberId_param+")가 존재하지 않습니다."));

        // 로그인 사용자의 마이페이지면
        if(memberId.equals(memberId_param)){
            // 프로젝트 엔티티 조회 (비공개 포함)
            pageable = projectRepository.findByMemberId(memberId, pageRequest);
        }else{ // 로그인 사용자의 마이페이지가 아니면
            // 프로젝트 엔티티 조회 (공개글만)
            pageable = projectRepository.findByMemberIdAndStatus(memberId, Post.Status.PUBLIC, pageRequest);
        }

        // 결과값이 없으면 204 반환
        if(!pageable.hasContent()) throw new CreaviCodeException(GlobalErrorCode.NOT_PROJECT_CONTENT);

        // 프로젝트 리스트 결과 toDto
        data = pageable.getContent().stream()
                .map(project -> ProjectListReadResponseDto.builder()
                        .id(project.getId())
                        .postType(PostType.PROJECT.name())
                        .category(project.getCategory().name())
                        .title(project.getTitle())
                        .links(project.getProjectLinks().stream().map(pl -> ProjectLinkResponseDto.builder().linkType(pl.getLinkType()).url(pl.getUrl()).build()).toList())
                        .thumbnail(project.getThumbnail())
                        .bannerContent(project.getBannerContent())
                        .status(project.getStatus().name())
                        .createdDate(project.getCreatedDate())
                        .memberNickname(project.getMember().getMemberNickname())
                        .memberProfile(project.getMember().getProfileUrl())
                        .build())
                .toList();

        // 성공 응답 반환
        return new SuccessResponseDto<>(true, "마이페이지 프로젝트 게시글 리스트 조회가 완료되었습니다.", data);
    }

    private static ProjectReadResponseDto toProjectReadResponseDto(Project project) {
        ProjectReadResponseDto data;

        // 프로젝트 맴버 포지션별 그룹화
        Map<String, List<Member>> positionMap = new HashMap<>();
        for(ProjectMember projectMember : project.getProjectMembers()){
            positionMap.computeIfAbsent(projectMember.getPosition(), k -> new ArrayList<>()).add(projectMember.getMember());
        }

        // 프로젝트 맴버 toDto
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

        // 프로젝트 링크 toDto
        List<ProjectLinkResponseDto> links = project.getProjectLinks().stream()
                .map(projectLink -> ProjectLinkResponseDto.builder()
                        .linkType(projectLink.getLinkType())
                        .url(projectLink.getUrl())
                        .build())
                .collect(Collectors.toList());

        // 프로젝트 기술스택 toDto
        List<ProjectTechStackResponseDto> techStacks = project.getProjectTechStacks().stream()
                .map(projectTechStack -> ProjectTechStackResponseDto.builder()
                        .techStack(projectTechStack.getTechStack().getTechStack())
                        .iconUrl(projectTechStack.getTechStack().getIconUrl())
                        .build())
                .collect(Collectors.toList());

        // 프로젝트 조회 결과 toDto
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
                .images(project.getProjectImages().stream().map(Image::getUrl).toList())
                .build();
        return data;
    }


}