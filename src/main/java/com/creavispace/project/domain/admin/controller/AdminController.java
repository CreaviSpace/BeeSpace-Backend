package com.creavispace.project.domain.admin.controller;


import com.creavispace.project.config.auth.utils.JwtUtil;
import com.creavispace.project.domain.admin.dto.MemberListDto;
import com.creavispace.project.domain.common.dto.response.SuccessResponseDto;
import com.creavispace.project.domain.community.dto.response.CommunityResponseDto;
import com.creavispace.project.domain.community.service.CommunityService;
import com.creavispace.project.domain.member.Role;
import com.creavispace.project.domain.member.dto.response.MemberJwtResponseDto;
import com.creavispace.project.domain.member.entity.Member;
import com.creavispace.project.domain.member.repository.MemberRepository;
import com.creavispace.project.domain.member.service.MemberService;
import com.creavispace.project.domain.project.dto.response.ProjectListReadResponseDto;
import com.creavispace.project.domain.project.service.ProjectService;
import com.creavispace.project.domain.recruit.dto.response.RecruitListReadResponseDto;
import com.creavispace.project.domain.recruit.service.RecruitService;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/admin")
@RestController
@RequiredArgsConstructor
public class AdminController {

    @Value("${jwt.secret}")
    private String jwtSecret;
    private final MemberRepository memberRepository;
    private final MemberService memberService;
    private final ProjectService projectService;
    private final RecruitService recruitService;
    private final CommunityService communityService;

    @PostMapping("/sanction")
    public void sanctionMember(@RequestBody String memberId, HttpServletRequest request) {
        String jwt = request.getHeader(HttpHeaders.AUTHORIZATION);
        MemberJwtResponseDto userInfo = JwtUtil.getUserInfo(jwt, jwtSecret);
        Member admin = memberRepository.findById(userInfo.memberId()).orElseThrow();
        if (!isAdmin(admin)) {
            return;
        }
        Member member = memberRepository.findById(memberId).orElseThrow();
        member.setExpired(true);
    }

    @GetMapping("/contents/project")
    public ResponseEntity<SuccessResponseDto<List<ProjectListReadResponseDto>>> projectContentsList(
            @RequestParam Integer size,
            @RequestParam Integer page) {
        System.out.println("AdminController.projectContentsList");
        SuccessResponseDto<List<ProjectListReadResponseDto>> projectList = projectService.readProjectList(size, page,
                null);
        return ResponseEntity.ok().body(projectList);
    }

    @GetMapping("/contents/recruit")
    public ResponseEntity<SuccessResponseDto<List<RecruitListReadResponseDto>>> recruitContentsList(
            @RequestParam Integer size,
            @RequestParam Integer page) {
        SuccessResponseDto<List<RecruitListReadResponseDto>> recruitList = recruitService.readRecruitList(size, page,
                null);
        return ResponseEntity.ok().body(recruitList);
    }

    @GetMapping("/contents/community")
    public ResponseEntity<SuccessResponseDto<List<CommunityResponseDto>>> communityContentsList(
            @RequestParam Integer size,
            @RequestParam Integer page) {
        SuccessResponseDto<List<CommunityResponseDto>> recruitList = communityService.readCommunityList(size, page,
                null, null, null);
        return ResponseEntity.ok().body(recruitList);
    }

    @PostMapping("/contents/delete")
    public void deleteContents(HttpServletRequest request, @RequestBody Long id, @RequestBody String category) {
        String jwt = request.getHeader(HttpHeaders.AUTHORIZATION);
        MemberJwtResponseDto userInfo = JwtUtil.getUserInfo(jwt, jwtSecret);
        deleteFactory(category, id, userInfo);
    }

    @GetMapping("/member")
    public List<MemberListDto> memberList(@RequestParam Integer size, @RequestParam Integer page, @RequestParam("sort-type") String sortType) {

        List<Member> members = memberService.findAllMembers(size, page, sortType);
        List<MemberListDto> collect = members.stream().map(member -> new MemberListDto(member)).collect(Collectors.toList());
        return collect;

    }

    private void deleteFactory(String category, Long id, MemberJwtResponseDto dto) {
        Member member = memberRepository.findById(dto.memberId()).orElseThrow();
        boolean isAdmin = isAdmin(member);
        if (!isAdmin) {
            return;
        }

        if (category.equals("project")) {
            projectService.deleteProject(member.getId(), id);
        }

        if (category.equals("recruit")) {
            recruitService.deleteRecruit(member.getId(), id);
        }

        if (category.equals("community")) {
            communityService.deleteCommunity(member.getId(), id);
        }
    }

    public boolean isAdmin(Member member) {
        return member.getRole().equals(Role.ADMIN);
    }

}
