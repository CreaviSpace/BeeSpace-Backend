package com.creavispace.project.domain.community.service;


import com.creavispace.project.domain.community.dto.request.CommunityRequestDto;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CommunityServiceImplTest {

    @Autowired
    private CommunityService communityService;

    @Test
    void createCommunity() {
        for (int i = 0; i < 10; i++) {
            List<String> hashTag = List.of("1", "2", "3", "4", "5", "6");
            CommunityRequestDto communityRequestDto = CommunityRequestDto.builder().title("커뮤니티 테스트 타이틀" + 1)
                    .content("커뮤니티 테스트 내용" + i)
                    .category("QNA").hashTags(hashTag).build();
            communityService.createCommunity("c43d714d", communityRequestDto);

        }
    }
}