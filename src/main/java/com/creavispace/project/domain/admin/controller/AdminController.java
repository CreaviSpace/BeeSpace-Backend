package com.creavispace.project.domain.admin.controller;


import com.creavispace.project.config.auth.utils.JwtUtil;
import com.creavispace.project.domain.admin.dto.DailySummary;
import com.creavispace.project.domain.admin.dto.MemberListDto;
import com.creavispace.project.domain.admin.dto.YearlySummary;
import com.creavispace.project.domain.admin.dto.request.DeleteRequestDto;
import com.creavispace.project.domain.admin.dto.MonthlySummary;
import com.creavispace.project.domain.admin.dto.request.MemberIdRequestDto;
import com.creavispace.project.domain.admin.entity.FiredMember;
import com.creavispace.project.domain.admin.repository.FiredMemberRepository;
import com.creavispace.project.domain.bookmark.entity.CommunityBookmark;
import com.creavispace.project.domain.bookmark.entity.ProjectBookmark;
import com.creavispace.project.domain.bookmark.entity.RecruitBookmark;
import com.creavispace.project.domain.bookmark.repository.CommunityBookmarkRepository;
import com.creavispace.project.domain.bookmark.repository.ProjectBookmarkRepository;
import com.creavispace.project.domain.bookmark.repository.RecruitBookmarkRepository;
import com.creavispace.project.domain.bookmark.service.BookmarkService;
import com.creavispace.project.domain.common.dto.response.SuccessResponseDto;
import com.creavispace.project.domain.community.dto.response.CommunityDeleteResponseDto;
import com.creavispace.project.domain.community.dto.response.CommunityResponseDto;
import com.creavispace.project.domain.community.service.CommunityService;
import com.creavispace.project.domain.member.Role;
import com.creavispace.project.domain.member.dto.response.MemberJwtResponseDto;
import com.creavispace.project.domain.member.entity.Member;
import com.creavispace.project.domain.member.repository.MemberRepository;
import com.creavispace.project.domain.member.service.MemberService;
import com.creavispace.project.domain.project.dto.response.ProjectDeleteResponseDto;
import com.creavispace.project.domain.project.dto.response.ProjectListReadResponseDto;
import com.creavispace.project.domain.project.service.ProjectService;
import com.creavispace.project.domain.recruit.dto.response.RecruitDeleteResponseDto;
import com.creavispace.project.domain.recruit.dto.response.RecruitListReadResponseDto;
import com.creavispace.project.domain.recruit.service.RecruitService;
import com.creavispace.project.domain.report.entity.Report;
import com.creavispace.project.domain.report.service.ReportService;
import com.creavispace.project.global.common.Service;
import com.creavispace.project.global.dto.DeleteResponseDto;
import com.creavispace.project.global.exception.CreaviCodeException;
import com.creavispace.project.global.exception.GlobalErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
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
    private final FiredMemberRepository firedMemberRepository;
    private final ProjectBookmarkRepository projectBookmarkRepository;
    private final RecruitBookmarkRepository recruitBookmarkRepository;
    private final CommunityBookmarkRepository communityBookmarkRepository;

    @PostMapping("/sanction")
    public void sanctionMember(@RequestBody MemberIdRequestDto dto, HttpServletRequest request) {
        String jwt = request.getHeader(HttpHeaders.AUTHORIZATION);
        MemberJwtResponseDto userInfo = JwtUtil.getUserInfo(jwt, jwtSecret);
        Member admin = memberRepository.findById(userInfo.memberId()).orElseThrow();
        if (!isAdmin(admin)) {
            return;
        }
        Member member = memberRepository.findById(dto.getMemberId()).orElseThrow();

        if (firedMemberRepository.existsById(dto.getMemberId())) {
            firedMemberRepository.deleteById(dto.getMemberId());
            member.setFired(false);
            memberRepository.save(member);
            return;
        }
        System.out.println("memberId = " + dto.getMemberId());
        member.setFired(true);
        memberRepository.save(member);

        firedMemberRepository.save(new FiredMember(dto.getMemberId(), dto.getReason(), LocalDateTime.now().plusDays(dto.getPeriod())));
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
        System.out.println("deleteRequestDto = " + deleteRequestDto.getCategory());
        System.out.println("deleteRequestDto = " + deleteRequestDto.getId());
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
            SuccessResponseDto<ProjectDeleteResponseDto> dto = projectService.deleteProject(
                    admin.getId(), id);
            ProjectDeleteResponseDto data = dto.getData();

            ProjectBookmark byProjectId = projectBookmarkRepository.findByProjectId(id)
                    .orElseThrow(IllegalArgumentException::new);
            byProjectId.setEnable(!byProjectId.isEnable());
            projectBookmarkRepository.save(byProjectId);

            return dtoSetting(dto, data);
        }

        if (category.equalsIgnoreCase("recruit")) {
            SuccessResponseDto<RecruitDeleteResponseDto> dto = recruitService.deleteRecruit(
                    admin.getId(), id);
            RecruitDeleteResponseDto data = dto.getData();

            RecruitBookmark recruitBookmark = recruitBookmarkRepository.findByRecruitId(id)
                    .orElseThrow(IllegalArgumentException::new);
            recruitBookmark.setEnable(!recruitBookmark.isEnable());
            recruitBookmarkRepository.save(recruitBookmark);
            return dtoSetting(dto, data);

        }

        if (category.equalsIgnoreCase("community")) {
            SuccessResponseDto<CommunityDeleteResponseDto> dto = communityService.deleteCommunity(admin.getId(), id);
            CommunityDeleteResponseDto data = dto.getData();

            CommunityBookmark communityBookmark = communityBookmarkRepository.findByCommunityId(id)
                    .orElseThrow(IllegalArgumentException::new);
            communityBookmark.setEnable(!communityBookmark.isEnable());
            communityBookmarkRepository.save(communityBookmark);
            return dtoSetting(dto, data);
        }
        return null;
    }

    private SuccessResponseDto<DeleteResponseDto> dtoSetting(SuccessResponseDto<?> dto,
            DeleteResponseDto data) {
        SuccessResponseDto<DeleteResponseDto> successResponseDto = new SuccessResponseDto<>();
        successResponseDto.setData(data);
        successResponseDto.setMessage(dto.getMessage());
        successResponseDto.setSuccess(true);
        return successResponseDto;
    }

    @GetMapping("/statistics/monthly")
    public SuccessResponseDto<List<MonthlySummary>> monthlyStatistics(@RequestParam int year, @RequestParam String category)
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Service service = getService(category);
        Method countMonthlySummary = service.getClass().getMethod("countMonthlySummary", int.class);
        SuccessResponseDto<List<MonthlySummary>> monthlySummaryByYearIn = (SuccessResponseDto<List<MonthlySummary>>) countMonthlySummary.invoke(service, year);
        return monthlySummaryByYearIn;
    }

    @GetMapping("/statistics/yearly")
    public SuccessResponseDto<List<YearlySummary>> yearlyStatistics(@RequestParam String category)
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Service service = getService(category);
        Method countMonthlySummary = service.getClass().getMethod("countYearlySummary");
        SuccessResponseDto<List<YearlySummary>> yearlySummary = (SuccessResponseDto<List<YearlySummary>>) countMonthlySummary.invoke(service);

        return yearlySummary;
    }

    @GetMapping("/statistics/daily")
    public SuccessResponseDto<List<DailySummary>> dailyStatistics(@RequestParam int year, @RequestParam int month, @RequestParam String category)
            throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        Service service = getService(category);
        Method countMonthlySummary = service.getClass().getMethod("countDailySummary", int.class, int.class);
        SuccessResponseDto<List<DailySummary>> dailySummaries = (SuccessResponseDto<List<DailySummary>>) countMonthlySummary.invoke(service, year, month);

        return dailySummaries;
    }


    private Service getService(String service) {
        if (service.equalsIgnoreCase("member")) {
            return memberService;
        }
        if (service.equalsIgnoreCase("project")) {
            return projectService;
        }
        if (service.equalsIgnoreCase("recruit")) {
            return recruitService;
        }
        if (service.equalsIgnoreCase("community")) {
            return communityService;
        }
        throw new IllegalArgumentException("서비스를 찾을 수 없습니다. :" + service);
    }


    public boolean isAdmin(Member member) {
        return member.getRole().equals(Role.ADMIN);
    }
}
