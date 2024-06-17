package com.creavispace.project.domain.project.service;

import com.creavispace.project.domain.admin.dto.DailySummary;
import com.creavispace.project.domain.admin.dto.MonthlySummary;
import com.creavispace.project.domain.admin.dto.YearlySummary;
import com.creavispace.project.common.dto.response.SuccessResponseDto;
import com.creavispace.project.common.dto.type.PostType;
import com.creavispace.project.common.dto.type.ProjectCategory;
import com.creavispace.project.domain.file.entity.Image;
import com.creavispace.project.domain.file.entity.ProjectImage;
import com.creavispace.project.domain.file.repository.ProjectImageRepository;
import com.creavispace.project.domain.file.service.FileService;
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
import com.creavispace.project.common.exception.CreaviCodeException;
import com.creavispace.project.common.exception.GlobalErrorCode;
import com.creavispace.project.common.utils.CustomValueOf;
import com.creavispace.project.common.utils.UsableConst;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
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
    private final ProjectMemberRepository projectMemberRepository;
    private final ProjectTechStackRepository projectTechStackRepository;
    private final ProjectLinkRepository projectLinkRepository;
    private final ProjectImageRepository projectImageRepository;
    private final FileService fileService;

    @Override
    @Transactional
    public SuccessResponseDto<ProjectResponseDto> createProject(String memberId, ProjectRequestDto dto) {
        ProjectResponseDto data = null;
        List<ProjectMemberRequestDto> memberDtos = dto.getMemberDtos();
        List<ProjectTechStackRequestDto> techStackDtos = dto.getTechStackDtos();
        List<ProjectLinkRequestDto> linkDtos = dto.getLinkDtos();
        List<String> images = dto.getImages();
        ProjectCategory categoryEnum = CustomValueOf.valueOf(ProjectCategory.class, dto.getCategory(), GlobalErrorCode.NOT_FOUND_PROJECT_CATEGORY);

        // JWT에 저장된 회원이 존재하는지
        Optional<Member> optionalMember = memberRepository.findById(memberId);
        Member member = optionalMember.orElseThrow(()-> new CreaviCodeException(GlobalErrorCode.MEMBER_NOT_FOUND));

        // 프로젝트 생성
        Project project = Project.builder()
                .member(member)
                .category(categoryEnum)
                .title(dto.getTitle())
                .field(dto.getField())
                .content(dto.getContent())
                .thumbnail(dto.getThumbnail())
                .bannerContent(dto.getBannerContent())
                .viewCount(0)
                .weekViewCount(0)
                .status(Boolean.TRUE)
                .build();

        // 프로젝트 저장
        projectRepository.save(project);

        // 프로젝트 이미지가 있다면
        if(images != null && !images.isEmpty()){
            List<String> contentImages = new ArrayList<>();

            Document doc = Jsoup.parse(dto.getContent());
            Elements imageElements = doc.select("img");

            for(Element imageElement : imageElements){
                String imageUrl = imageElement.attr("src");
                contentImages.add(imageUrl);
            }

            List<String> deletedImg = new ArrayList<>(images);
            deletedImg.removeAll(contentImages);

            fileService.deleteImages(deletedImg);

            List<String> saveImg = new ArrayList<>(images);
            saveImg.removeAll(deletedImg);

            List<ProjectImage> saveProjectImages = saveImg.stream().map(img -> new ProjectImage(project,img)).toList();

            projectImageRepository.saveAll(saveProjectImages);

        }

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
                        TechStack projectTechStack = techStackRepository.findById(techStackDto.getTechStack()).orElseThrow(()-> new CreaviCodeException(GlobalErrorCode.TECHSTACK_NOT_FOUND));
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
        List<ProjectImage> projectImages = projectImageRepository.findByProjectId(projectId);

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
                .images(projectImages.stream().map(Image::getUrl).toList())
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
        List<String> images = dto.getImages();

        // JWT에 저장된 회원이 존재하는지
        Optional<Member> optionalMember = memberRepository.findById(memberId);
        Member member = optionalMember.orElseThrow(()-> new CreaviCodeException(GlobalErrorCode.MEMBER_NOT_FOUND));

        // 수정할 프로젝트 게시글이 존재하는지
        Optional<Project> optionalProject = projectRepository.findById(projectId);
        Project project = optionalProject.orElseThrow(()-> new CreaviCodeException(GlobalErrorCode.PROJECT_NOT_FOUND));

        // 수정할 권한이 있는지
        if(!memberId.equals(project.getMember().getId()) && !member.getRole().equals(Role.ADMIN)){
            throw new CreaviCodeException(GlobalErrorCode.NOT_PERMISSMISSION);
        }

        // 프로젝트 수정 및 저장
        project.modify(dto);
        projectRepository.save(project);

        // 프로젝트 이미지가 있다면
        if(images != null && !images.isEmpty()){
            List<String> contentImages = new ArrayList<>();

            Document doc = Jsoup.parse(dto.getContent());
            Elements imageElements = doc.select("img");

            for(Element imageElement : imageElements){
                String imageUrl = imageElement.attr("src");
                contentImages.add(imageUrl);
            }

            List<String> deletedImg = new ArrayList<>(images);
            deletedImg.removeAll(contentImages);

            fileService.deleteImages(deletedImg);

            List<String> saveImg = new ArrayList<>(images);
            saveImg.removeAll(deletedImg);

            List<ProjectImage> saveProjectImages = saveImg.stream().map(img -> new ProjectImage(project,img)).toList();

            projectImageRepository.deleteByProjectId(projectId);
            projectImageRepository.saveAll(saveProjectImages);

        }

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
                        TechStack projectTechStack = techStackRepository.findById(techStackDto.getTechStack()).orElseThrow(()-> new CreaviCodeException(GlobalErrorCode.TECHSTACK_NOT_FOUND));
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
        List<ProjectImage> projectImages = projectImageRepository.findByProjectId(projectId);

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
                .images(projectImages.stream().map(Image::getUrl).toList())
                .build();

        log.info("/project/service : modifyProject success data = {}", data);
        // 성공 응답 반환
        return new SuccessResponseDto<>(true, "프로젝트 게시글의 수정이 완료되었습니다.", data);
    }

    @Override
    @Transactional
    public SuccessResponseDto<ProjectDeleteResponseDto> deleteProject(String memberId, Long projectId) {
        ProjectDeleteResponseDto data = null;
        System.out.println("ProjectServiceImpl.deleteProject");

        // JWT에 저장된 회원이 존재하는지
        Optional<Member> optionalMember = memberRepository.findById(memberId);
        Member member = optionalMember.orElseThrow(()-> new CreaviCodeException(GlobalErrorCode.MEMBER_NOT_FOUND));

        // 삭제할 프로젝트 게시글이 존재하는지
        Project project = projectRepository.findById(projectId)
                .orElseThrow(()-> new CreaviCodeException(GlobalErrorCode.PROJECT_NOT_FOUND));

        // 삭제할 권한이 있는지
        if(!memberId.equals(project.getMember().getId()) && !member.getRole().equals(Role.ADMIN)){
            throw new CreaviCodeException(GlobalErrorCode.NOT_PERMISSMISSION);
        }

        // 비활성화 변경 및 저장
        boolean toggle = project.disable();
        projectRepository.save(project);

        // 프로젝트 삭제 결과 DTO
        data = ProjectDeleteResponseDto.builder()
                .projectId(project.getId())
                .postType(PostType.PROJECT.name())
                .build();

        log.info("/project/service : deleteProject success data = {}", data);

        // 성공 응답 반환
        if (toggle) {
            return new SuccessResponseDto<>(true, "프로젝트 게시글 복구가 완료되었습니다.", data);
        }
        return new SuccessResponseDto<>(true, "프로젝트 게시글 삭제가 완료되었습니다.", data);
    }
    @Override
    @Transactional
    public SuccessResponseDto<ProjectDeleteResponseDto> deleteMemberProject(String memberId) {
        ProjectDeleteResponseDto data = null;
        System.out.println("ProjectServiceImpl.deleteProject");

        // JWT에 저장된 회원이 존재하는지
        Optional<Member> optionalMember = memberRepository.findById(memberId);
        Member member = optionalMember.orElseThrow(()-> new CreaviCodeException(GlobalErrorCode.MEMBER_NOT_FOUND));
        boolean toggle = member.isExpired();

        // 삭제할 프로젝트 게시글이 존재하는지
        List<Project> project = projectRepository.findAllByMemberId(memberId);

        if (project.size() == 0) {
            return null;
        }
        long count = project.stream().filter(p -> !memberId.equals(p.getMember().getId()) && p.getMember().getRole().equals(Role.ADMIN)).count();
        // 삭제할 권한이 있는지
        if(count != 0 && !member.getRole().equals(Role.ADMIN)){
            throw new CreaviCodeException(GlobalErrorCode.NOT_PERMISSMISSION);
        }

        // 비활성화 변경 및 저장

        project.stream().map(Project::disable);

        projectRepository.saveAll(project);

        // 프로젝트 삭제 결과 DTO
        data = ProjectDeleteResponseDto.builder()
                .projectId(9999L)
                .postType(PostType.PROJECT.name())
                .build();

        log.info("/project/service : deleteProject success data = {}", data);

        // 성공 응답 반환
        if (toggle) {
            return new SuccessResponseDto<>(true, "프로젝트 게시글 복구가 완료되었습니다.", data);
        }
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
    public SuccessResponseDto<List<ProjectListReadResponseDto>> readProjectList(Pageable pageRequest, ProjectCategory category) {
        List<ProjectListReadResponseDto> data = null;

        String categoryStr = category == null ? null : category.name();

        Page<Project> pageable = projectRepository.findAllByStatusTrueAndCategory(categoryStr, pageRequest);

        // 해당 페이지에 게시글이 있는지
        if(!pageable.hasContent()) throw new CreaviCodeException(GlobalErrorCode.NOT_PROJECT_CONTENT);

        // 프로젝트 리스트 결과 DTO
        data = getProjectListReadResponseDtos(pageable);

        log.info("/project/service : readProjectList success data = {}", data);
        // 성공 응답 반환
        return new SuccessResponseDto<>(true, "프로젝트 게시글 리스트 조회가 완료되었습니다.", data);
    }

    @Override
    @Transactional
    public SuccessResponseDto<ProjectReadResponseDto> readProject(String memberId, Long projectId, HttpServletRequest request) {
        ProjectReadResponseDto data = null;
        Optional<Project> optionalProject;

        Project project = projectRepository.findById(projectId).orElseThrow(() -> new CreaviCodeException(GlobalErrorCode.PROJECT_NOT_FOUND));

        if(!project.isStatus() && !project.getMember().getId().equals(memberId)){
            throw new CreaviCodeException(GlobalErrorCode.PROJECT_NOT_FOUND);
        }

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
                        .techStack(projectTechStack.getTechStack().getTechStack())
                        .iconUrl(projectTechStack.getTechStack().getIconUrl())
                        .build())
                .collect(Collectors.toList());

        List<ProjectImage> projectImages = projectImageRepository.findByProjectId(projectId);

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
                .images(projectImages.stream().map(Image::getUrl).toList())
                .build();

        log.info("/project/service : readProject success data = {}", data);
        // 성공 응답 반환
        return new SuccessResponseDto<>(true, "프로젝트 게시글 상세조회가 완료되었습니다.", data);
    }


    //ky

    @Override
    public SuccessResponseDto<List<ProjectListReadResponseDto>> readMyProjectList(
            String memberId, Integer size, Integer page, String projectCategory, String sortType) {
        Pageable pageRequest = UsableConst.getPageRequest(size, page, sortType);
        Page<Project> pageable = findByProjectCategory(memberId, projectCategory, pageRequest);

        if (!pageable.hasContent()) {
            throw new CreaviCodeException(GlobalErrorCode.NOT_PROJECT_CONTENT);
        }

        List<ProjectListReadResponseDto> reads = getProjectListReadResponseDtos(pageable);

        return new SuccessResponseDto<>(true, "프로젝트 게시글 리스트 조회가 완료되었습니다.", reads);
    }

    @Override//////////////////////////////////////////
    public SuccessResponseDto<List<ProjectListReadResponseDto>> readMyProjectFeedBackList(
            String memberId, Integer size, Integer page, String sortType) {
        Pageable pageRequest = UsableConst.getPageRequest(size, page, sortType);
        Page<Project> pageable = findByProjectCategory(memberId, null, pageRequest);

        if (!pageable.hasContent()) {
            throw new CreaviCodeException(GlobalErrorCode.NOT_PROJECT_CONTENT);
        }

        List<Project> projects = pageable.stream().filter(Project::isFeedback).collect(Collectors.toList());
        List<ProjectListReadResponseDto> reads = getProjectListReadResponseDtos(projects);

        return new SuccessResponseDto<>(true, "프로젝트 게시글 리스트 조회가 완료되었습니다.", reads);
    }

    @Override
    public SuccessResponseDto<List<ProjectListReadResponseDto>> readProjectListForAdmin(
            Integer size, Integer page, String status, String sortType) {

        Pageable pageRequest = UsableConst.getPageRequest(size, page, sortType);
        Page<Project> pageable = findProject(status, pageRequest);
        List<ProjectListReadResponseDto> projectListReadResponseDtos = getProjectListReadResponseDtos(pageable);

        return new SuccessResponseDto<>(true, "프로젝트 게시글 리스트 조회가 완료되었습니다.", projectListReadResponseDtos);
    }

    @Override
    public SuccessResponseDto<List<MonthlySummary>> countMonthlySummary(int year) {
        return new SuccessResponseDto(true,"월별 프로젝트 게시물 통계 조회 완료", projectRepository.countMonthlySummary(year));
    }

    @Override
    public SuccessResponseDto<List<YearlySummary>> countYearlySummary() {
        return new SuccessResponseDto(true,"연간 프로젝트 게시물 통계 조회 완료", projectRepository.countYearlySummary());
    }

    @Override
    public SuccessResponseDto<List<DailySummary>> countDailySummary(int year, int month) {
        return new SuccessResponseDto(true,"일간 프로젝트 게시물 통계 조회 완료", projectRepository.countDailySummary(year, month));
    }

    private Page<Project> findProject(String status, Pageable pageRequest) {
        System.out.println("ProjectServiceImpl.findProject");
        if (status.equalsIgnoreCase("all")) {
            return projectRepository.findAll(pageRequest);
        }
        if (status.equalsIgnoreCase("false")) {
             return projectRepository.findByStatusFalse(pageRequest);
        }
        if (status.equalsIgnoreCase("true")) {
            return projectRepository.findAllByStatusTrue(pageRequest);
        }
        return projectRepository.findAll(pageRequest);
    }

    private Page<Project> findByProjectCategory(String memberId, String category, Pageable pageRequest) {
        ProjectCategory projectCategory = ProjectCategory.getProjectCategory(category);
        if (projectCategory != null) {
            return projectRepository.findAllByStatusTrueAndCategoryAndMemberId(projectCategory, memberId, pageRequest);
        }
        return projectRepository.findAllByStatusTrueAndMemberId(memberId, pageRequest);
    }

    private List<ProjectListReadResponseDto> getProjectListReadResponseDtos(List<Project> projects) {
        System.out.println("ProjectServiceImpl.getProjectListReadResponseDtos");
        List<String> memberIds = projects.stream().map(project -> project.getMember().getId()).toList();
        List<Long> projectIds = projects.stream().map(Project::getId).collect(Collectors.toList());

        memberRepository.findByIdIn(memberIds);
        List<ProjectLink> byProjectIdIn = projectLinkRepository.findByProjectIdIn(projectIds);

        Map<Long, List<ProjectLinkResponseDto>> links = projectLinks(byProjectIdIn);

        return projects.stream()
                .map(project -> buildDto(project, links)
                ).collect(Collectors.toList());
    }

    private List<ProjectListReadResponseDto> getProjectListReadResponseDtos(Page<Project> projects) {

        System.out.println("ProjectServiceImpl.getProjectListReadResponseDtos");
        List<String> memberIds = projects.stream().map(project -> project.getMember().getId()).toList();
        List<Long> projectIds = projects.stream().map(Project::getId).collect(Collectors.toList());

        memberRepository.findByIdIn(memberIds);
        List<ProjectLink> byProjectIdIn = projectLinkRepository.findByProjectIdIn(projectIds);

        Map<Long, List<ProjectLinkResponseDto>> links = projectLinks(byProjectIdIn);

        return projects.stream()
                .map(project -> buildDto(project, links)
                ).collect(Collectors.toList());
    }

    private Map<Long, List<ProjectLinkResponseDto>> projectLinks(List<ProjectLink> projectLinks) {
        Map<Long, List<ProjectLinkResponseDto>> linkMap = new HashMap<>();
        for (int i = 0; i < projectLinks.size(); i++) {
            ProjectLink projectLink = projectLinks.get(i);
            List<ProjectLinkResponseDto> links = linkMap.getOrDefault(projectLink.getProject().getId(),
                    new ArrayList<>());
            ProjectLinkResponseDto projectLinkResponseDto = ProjectLinkResponseDto.builder()
                    .linkType(projectLink.getLinkType())
                    .url(projectLink.getUrl())
                    .build();
            links.add(projectLinkResponseDto);
            linkMap.put(projectLink.getProject().getId(), links);
        }
        return linkMap;
    }

    private ProjectListReadResponseDto buildDto(Project project, Map<Long, List<ProjectLinkResponseDto>> links) {
        return ProjectListReadResponseDto.builder()
                .memberProfile(project.getMember().getProfileUrl())
                .memberNickname(project.getMember().getMemberNickname())
                .id(project.getId())
                .postType(PostType.PROJECT.name())
                .category(project.getCategory().name())
                .title(project.getTitle())
                .thumbnail(project.getThumbnail())
                .bannerContent(project.getBannerContent())
                .links(links.get(project.getId()))
                .status(project.isStatus())
                .createdDate(project.getCreatedDate())
                .build();

    }
}