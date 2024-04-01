package com.creavispace.project.domain.mypage.controller;

import com.creavispace.project.config.auth.utils.JwtUtil;
import com.creavispace.project.domain.common.dto.response.SuccessResponseDto;
import com.creavispace.project.domain.member.dto.response.MemberJwtResponseDto;
import com.creavispace.project.domain.member.entity.Member;
import com.creavispace.project.domain.member.service.MemberService;
import com.creavispace.project.domain.mypage.dto.request.MyPageModifyRequestDto;
import com.creavispace.project.domain.mypage.dto.response.MyPageContentsResponseDto;
import com.creavispace.project.domain.mypage.dto.response.MyPageProfileResponseDto;
import com.creavispace.project.domain.project.dto.response.ProjectListReadResponseDto;
import com.creavispace.project.domain.project.service.ProjectService;
import com.creavispace.project.domain.recruit.service.RecruitService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
public class MyPageController {

    private final MemberService memberService;
    private final ProjectService projectService;
    private final RecruitService recruitService;


    @Value("${jwt.secret}")
    private String secretKey;

    @GetMapping("/mypage")
    @Operation(summary = "프로필 조회")
    public ResponseEntity<MyPageProfileResponseDto> readMyProfile(HttpServletRequest request) {
        System.out.println("MyPageController.readMyProfile");
        Optional<Member> memberInfo = getMember(request);

        return memberInfo.map(member -> ResponseEntity.status(HttpStatus.OK).body(new MyPageProfileResponseDto(member)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MyPageProfileResponseDto()));
    }

    @GetMapping("/mypage/mycontent/all")
    @Operation(summary = "내 작성글 조회")
    public ResponseEntity<MyPageContentsResponseDto> readMyAllContents(HttpServletRequest request,
                                                                       @RequestParam("size") Integer size,
                                                                       @RequestParam("page") Integer page) {
        //asc desc
        log.info("readMyAllContents");
        Optional<Member> member = getMember(request);

        if (member.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body(new MyPageContentsResponseDto());

        }
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    @GetMapping("/mypage/mycontent/project")
    @Operation(summary = "내 작성글 조회")
    public ResponseEntity<MyPageContentsResponseDto> readMyProjectContents(HttpServletRequest request,
                                                                           @RequestParam("size") Integer size,
                                                                           @RequestParam("page") Integer page,
                                                                           @RequestParam("sortType") String sortType) {
        log.info("readMyProjectContents");
        Optional<Member> member = getMember(request);

        if (member.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body(new MyPageContentsResponseDto());

        }
        SuccessResponseDto<List<ProjectListReadResponseDto>> myContents = projectService.readMyProjectList(member.get(),
                size, page, sortType);
        MyPageContentsResponseDto responseDto = new MyPageContentsResponseDto(myContents);
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @GetMapping("/mypage/mycontent/recruit")
    @Operation(summary = "내 작성글 조회")
    public ResponseEntity<MyPageContentsResponseDto> readMyRecruitContents(HttpServletRequest request,
                                                                           @RequestParam("size") Integer size,
                                                                           @RequestParam("page") Integer page,
                                                                           @RequestParam("sortType") String sortType) {
        log.info("readMyProjectContents");
        Optional<Member> member = getMember(request);

        if (member.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body(new MyPageContentsResponseDto());
        }

        SuccessResponseDto<List<ProjectListReadResponseDto>> myContents = null;
        MyPageContentsResponseDto responseDto = new MyPageContentsResponseDto(myContents);
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @GetMapping("/mypage/mycontent/community")
    @Operation(summary = "내 작성글 조회")
    public ResponseEntity<MyPageContentsResponseDto> readMyCommunityContents(HttpServletRequest request,
                                                                             @RequestParam("size") Integer size,
                                                                             @RequestParam("page") Integer page,
                                                                             @RequestParam("sortType") String sortType) {
        log.info("readMyProjectContents");
        Optional<Member> member = getMember(request);

        if (member.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body(new MyPageContentsResponseDto());

        }
        SuccessResponseDto<List<ProjectListReadResponseDto>> myContents = null;
        MyPageContentsResponseDto responseDto = new MyPageContentsResponseDto(myContents);
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @PostMapping("mypage/edit")
    @Operation(summary = "마이페이지 수정")
    public ResponseEntity<MyPageProfileResponseDto> modifyProfile(HttpServletRequest request,
                                                                  @RequestBody MyPageModifyRequestDto requestDto) {
        Optional<Member> member = getMember(request);

        if (member.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body(new MyPageProfileResponseDto());

        }

        memberService.update(member.get(), requestDto);

        return ResponseEntity.status(HttpStatus.OK).body(new MyPageProfileResponseDto());
    }


    private Optional<Member> getMember(HttpServletRequest request) {
        final String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);

        MemberJwtResponseDto memberInfo = JwtUtil.getUserInfo(authorization, secretKey);
        Optional<Member> member = memberService.findByEmailAndLoginTypeAndMemberId(memberInfo.memberEmail(),
                memberInfo.loginType(), memberInfo.memberId());
        return member;
    }
}
