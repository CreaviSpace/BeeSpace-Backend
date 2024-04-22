package com.creavispace.project.domain.project.service;

import com.creavispace.project.domain.common.dto.response.SuccessResponseDto;
import com.creavispace.project.domain.common.dto.type.ProjectCategory;
import com.creavispace.project.domain.member.entity.Member;
import com.creavispace.project.domain.member.repository.MemberRepository;
import com.creavispace.project.domain.project.dto.request.ProjectLinkRequestDto;
import com.creavispace.project.domain.project.dto.request.ProjectMemberRequestDto;
import com.creavispace.project.domain.project.dto.request.ProjectRequestDto;
import com.creavispace.project.domain.project.dto.request.ProjectTechStackRequestDto;
import com.creavispace.project.domain.project.dto.response.ProjectListReadResponseDto;
import com.creavispace.project.domain.techStack.entity.TechStack;
import com.creavispace.project.domain.techStack.repository.TechStackRepository;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest
class ProjectServiceImplTest {

    @Autowired
    ProjectService projectService;

    @Autowired
    TechStackRepository techStackRepository;

    @Autowired
    MemberRepository memberRepository;

    @Test
    @Commit
    void createRepeat() {
        for (int i = 0; i < 10; i ++) {
            createProject();
        }
    }
    @Test
    @Commit
    void createProject() {
        ProjectLinkRequestDto linkRequestDto = ProjectLinkRequestDto.builder()
                .url("")
                .linkType("")
                .build();
        ProjectLinkRequestDto linkRequestDto1 = ProjectLinkRequestDto.builder()
                .url("")
                .linkType("")
                .build();

        ProjectMemberRequestDto projectMemberRequestDto = ProjectMemberRequestDto.
                builder().
                memberId("6861d93b").
                position("")
                .build();

        List<ProjectLinkRequestDto> projectLinkRequestDtos = List.of(linkRequestDto, linkRequestDto1);

        ProjectTechStackRequestDto techStackRequestDto = new ProjectTechStackRequestDto("프론트엔드");

        ProjectRequestDto projectRequestDto = ProjectRequestDto.builder().category("TEAM")
                .linkDtos(projectLinkRequestDtos)
                .memberDtos(List.of(projectMemberRequestDto))
                .title("타이틀 1")
                .field("")
                .techStackDtos(List.of(techStackRequestDto))
                .bannerContent("banner")
                .thumbnail("thumnail")
                .content("테스트 내용")
                .build();

        projectService.createProject("6861d93b", projectRequestDto);
    }

    @Test
    @Commit
    void techStack() {
        TechStack techStack =TechStack.builder().techStack("프론트엔드").iconUrl("").build();
        techStackRepository.save(techStack);
    }

    @Test
    @Commit
    void readMyProject() {
        Member member = memberRepository.findById("").orElseThrow();

        SuccessResponseDto<List<ProjectListReadResponseDto>> listSuccessResponseDto = projectService.readMyProjectList(
                member.getId(), 6, 1, ProjectCategory.TEAM.name(), "asc");

        listSuccessResponseDto.getData().forEach(System.out::println);
    }
}