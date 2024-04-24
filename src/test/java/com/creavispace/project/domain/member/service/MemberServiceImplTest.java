package com.creavispace.project.domain.member.service;

import com.creavispace.project.domain.member.dto.response.MemberResponseDto;
import com.creavispace.project.domain.member.entity.Member;
import com.creavispace.project.domain.techStack.repository.TechStackRepository;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MemberServiceImplTest {

    @Autowired
    MemberService memberService;
    @Autowired
    TechStackRepository techStackRepository;

    @Test
    void findByMemberNicknameOrIdTagContaining() {
        List<MemberResponseDto> data = memberService.findByMemberNicknameOrIdContaining("0");
        System.out.println("data = " + data);
    }

    @Test
    void findContents() {
        memberService.findAllMembers(1, 1, "asc");
    }

    @Test
    void update() {
        Member member = memberService.findById("71c37a9f");
        String techStack = techStackRepository.findById("자바").orElseThrow().getTechStack();
        String techStack1 = techStackRepository.findById("자바스크립트").orElseThrow().getTechStack();
        member.setInterestedStack(new ArrayList<>(List.of(techStack, techStack1)));
        memberService.save(member);
    }

    @Test
    void check() {
        Member member = memberService.findById("71c37a9f");
        System.out.println("member = " + member.getInterestedStack());
    }
}