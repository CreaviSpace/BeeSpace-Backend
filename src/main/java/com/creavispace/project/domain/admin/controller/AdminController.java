package com.creavispace.project.domain.admin.controller;


import com.creavispace.project.config.auth.utils.JwtUtil;
import com.creavispace.project.domain.admin.dto.MemberListDto;
import com.creavispace.project.domain.admin.dto.request.DeleteRequestDto;
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
import com.creavispace.project.domain.report.entity.Report;
import com.creavispace.project.domain.report.service.ReportService;
import com.creavispace.project.global.dto.DeleteResponseDto;
import com.creavispace.project.global.exception.CreaviCodeException;
import com.creavispace.project.global.exception.GlobalErrorCode;
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
import static com.creavispace.project.global.util.UsableConst.*;
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
    private final ReportService reportService;

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

    @GetMapping("/contents/project")//
    public ResponseEntity<SuccessResponseDto<List<ProjectListReadResponseDto>>> projectContentsList(
            @RequestParam(name = "size") Integer size,
            @RequestParam(name = "page") Integer page,
            @RequestParam(name = "status") String status,
            @RequestParam(name = SORT_TYPE) String sortType) {
        System.out.println("AdminController.projectContentsList");
        SuccessResponseDto<List<ProjectListReadResponseDto>> projectList = projectService.readProjectListForAdmin(size, page, status, sortType);
        return ResponseEntity.ok().body(projectList);
    }

    @GetMapping("/contents/recruit")//
    public ResponseEntity<SuccessResponseDto<List<RecruitListReadResponseDto>>> recruitContentsList(
            @RequestParam(name = "size") Integer size,
            @RequestParam(name = "page") Integer page,
            @RequestParam(name = "status") String status,
            @RequestParam(name = SORT_TYPE) String sortType) {
        System.out.println("AdminController.recruitContentsList");
        SuccessResponseDto<List<RecruitListReadResponseDto>> recruitList = recruitService.readRecruitListForAdmin(size, page,
                status, sortType);
        return ResponseEntity.ok().body(recruitList);
    }

    @GetMapping("/contents/community")
    public ResponseEntity<SuccessResponseDto<List<CommunityResponseDto>>> communityContentsList(
            @RequestParam(name = "size") Integer size,
            @RequestParam(name = "page") Integer page,
            @RequestParam(name = "status") String status,
            @RequestParam(name = SORT_TYPE) String sortType) {
        SuccessResponseDto<List<CommunityResponseDto>> recruitList = communityService.readCommunityListForAdmin(size, page,
                status, sortType);
        return ResponseEntity.ok().body(recruitList);
    }

    @PostMapping("/contents/delete")
    public SuccessResponseDto<DeleteResponseDto> deleteContents(HttpServletRequest request, @RequestBody DeleteRequestDto deleteRequestDto) {
        System.out.println("AdminController.deleteContents");
        String jwt = request.getHeader(HttpHeaders.AUTHORIZATION);
        MemberJwtResponseDto userInfo = JwtUtil.getUserInfo(jwt, jwtSecret);
        Member admin = memberRepository.findById(userInfo.memberId()).orElseThrow();
        if (!isAdmin(admin)) {
             throw new CreaviCodeException(GlobalErrorCode.NOT_PERMISSMISSION);
        }
        System.out.println("admin = " + admin);
        SuccessResponseDto<DeleteResponseDto> successResponseDto = deleteFactory(deleteRequestDto.getCategory(),
                deleteRequestDto.getId(), admin);
        return successResponseDto;
    }

    @GetMapping("/member")
    public List<MemberListDto> memberList(@RequestParam Integer size, @RequestParam Integer page, @RequestParam(SORT_TYPE) String sortType) {

        List<Member> members = memberService.findAllMembers(size, page, sortType);
        List<MemberListDto> collect = members.stream().map(member -> new MemberListDto(member)).collect(Collectors.toList());
        return collect;

    }

    @GetMapping("/reports")
    public List<Report> reportList(@RequestParam Integer page, @RequestParam Integer size,
                                   @RequestParam(SORT_TYPE) String sortType) {
        return reportService.readReportList(size, page, sortType);
    }

    private SuccessResponseDto<DeleteResponseDto> deleteFactory(String category, Long id, Member admin) {
        SuccessResponseDto<DeleteResponseDto> successResponseDto = new SuccessResponseDto<>();

        if (category.equalsIgnoreCase("project")) {
            DeleteResponseDto data = projectService.deleteProject(
                    admin.getId(), id).getData();
            successResponseDto.setData(data);
            successResponseDto.setMessage("프로젝트 게시글 삭제가 완료되었습니다");
            successResponseDto.setSuccess(true);
            return successResponseDto;
        }

        if (category.equalsIgnoreCase("recruit")) {
            DeleteResponseDto data = recruitService.deleteRecruit(admin.getId(), id).getData();
            successResponseDto.setData(data);
            successResponseDto.setMessage("모집 게시글 삭제가 완료되었습니다");
            successResponseDto.setSuccess(true);
            return successResponseDto;

        }

        if (category.equalsIgnoreCase("community")) {
            DeleteResponseDto data = communityService.deleteCommunity(admin.getId(), id).getData();
            successResponseDto.setData(data);
            successResponseDto.setMessage("커뮤니티 게시글 삭제가 완료되었습니다");
            successResponseDto.setSuccess(true);
            return successResponseDto;
        }
        return null;
    }

    public boolean isAdmin(Member member) {
        return member.getRole().equals(Role.ADMIN);
    }

}
