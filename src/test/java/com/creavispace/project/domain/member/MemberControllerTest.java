package com.creavispace.project.domain.member;

import com.creavispace.project.domain.bookmark.service.BookmarkService;
import com.creavispace.project.common.dto.response.SuccessResponseDto;
import com.creavispace.project.common.dto.type.ProjectCategory;
import com.creavispace.project.domain.member.dto.response.MemberResponseDto;
import com.creavispace.project.domain.member.service.MemberService;
import com.creavispace.project.domain.project.dto.response.ProjectListReadResponseDto;
import com.creavispace.project.domain.project.service.ProjectService;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class MemberControllerTest {
    @Autowired
    private ProjectService projectService;
    @Autowired
    private MemberService memberService;

    @Autowired
    private BookmarkService bookmarkService;
    @Test
    void projectServiceTest() throws JsonProcessingException {
        MemberResponseDto member = new MemberResponseDto(memberService.findById(""));
        System.out.println("member = " + member);
        SuccessResponseDto<List<ProjectListReadResponseDto>> listSuccessResponseDto = projectService.readMyProjectList(
                member.getMemberId(), 2, 1, ProjectCategory.INDIVIDUAL.name(), "asc");
        listSuccessResponseDto.getData().forEach(a -> System.out.println("a = " + a));
    }

}
