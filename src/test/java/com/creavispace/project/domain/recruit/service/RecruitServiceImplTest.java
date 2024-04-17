package com.creavispace.project.domain.recruit.service;


import com.creavispace.project.domain.recruit.dto.request.RecruitPositionRequestDto;
import com.creavispace.project.domain.recruit.dto.request.RecruitRequestDto;
import com.creavispace.project.domain.recruit.dto.request.RecruitTechStackRequestDto;
import com.creavispace.project.domain.techStack.repository.TechStackRepository;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class RecruitServiceImplTest {

    @Autowired
    RecruitService recruitService;

    @Autowired
    TechStackRepository techStackRepository;


    @Test
    void recruitServiceWriteContent() {
        for (int i = 0; i < 10; i++) {
            List<RecruitTechStackRequestDto> techStack = new ArrayList(Arrays.asList(RecruitTechStackRequestDto.builder().techStackId(2L).build()));
            List<RecruitPositionRequestDto> positions = new ArrayList<>(Arrays.asList(RecruitPositionRequestDto.builder().position("프론트엔드").now(0).amount(5).build()));
            RecruitRequestDto recruitRequestDto = RecruitRequestDto.builder().content("테스트 모집 게시물").category("PROJECT_RECRUIT")
                    .contactWay("EMAIL")
                    .proceedWay("OFFLINE").amount(1).workDay(30).contact("kyuyoungk@naver.com").end("2030,03,03")
                    .endFormat("yyyy,MM,dd")
                    .title("테스트 모집 타이틀").techStacks(techStack).positions(positions).build();
            recruitService.createRecruit("80c78372", recruitRequestDto);
        }
    }

}
