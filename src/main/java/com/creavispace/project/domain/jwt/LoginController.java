package com.creavispace.project.domain.jwt;

import com.creavispace.project.domain.jwt.dto.JwtDto;
import com.creavispace.project.domain.member.dto.MemberFiredDto;
import com.creavispace.project.common.utils.JwtManager;
import com.creavispace.project.domain.admin.entity.FiredMember;
import com.creavispace.project.domain.admin.repository.FiredMemberRepository;
import com.creavispace.project.domain.member.service.MemberService;
import com.creavispace.project.common.Dto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import static com.creavispace.project.common.utils.UsableConst.*;

@RequiredArgsConstructor
@RestController
@Slf4j
public class LoginController {

    @Value("${jwt.secret}")
    private String jwtSecret;

    private final MemberService memberService;
    private final FiredMemberRepository firedMemberRepository;

    @GetMapping("/login")
    public ResponseEntity<Dto> login(
            @RequestParam("token") String tokenName) {
        System.out.println("LoginController.login");

        return getJwt(tokenName);
    }

    private ResponseEntity<Dto> getJwt(String tokenName) {
        JwtDto token = JwtManager.findToken(tokenName);
        if (token.isFired()) {
            FiredMember firedMember = firedMemberRepository.findById(token.getMemberId()).orElseThrow();
            MemberFiredDto loginFail = new MemberFiredDto(AUTH_FAIL, firedMember.getReason(), firedMember.getCreatedDate(),
                    firedMember.getDeadLine());
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(loginFail);
        }
        return ResponseEntity.status(HttpStatus.OK).body(token);
    }
}
