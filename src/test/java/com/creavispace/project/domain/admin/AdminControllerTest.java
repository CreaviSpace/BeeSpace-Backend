package com.creavispace.project.domain.admin;

import static org.assertj.core.api.Assertions.assertThat;

import com.creavispace.project.domain.admin.controller.AdminController;
import com.creavispace.project.domain.member.entity.Member;
import com.creavispace.project.domain.member.repository.MemberRepository;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@SpringBootTest
public class AdminControllerTest {

    @Autowired
    private MemberRepository memberRepository;

    private AdminController controller = new AdminController(null,null,null,null,null, null);

    @Test
    void isAdminTest() {
        Member member = memberRepository.findById("").get();
        System.out.println("member.getRole() = " + member.getRole());
        assertThat(controller.isAdmin(member)).isTrue();
    }

    @Test
    void memberListtest() {
        Pageable pageRequest = PageRequest.of(1 - 1, 6);
        List<Member> all = memberRepository.findBy(pageRequest);
        all.forEach(System.out::println);

    }
}
