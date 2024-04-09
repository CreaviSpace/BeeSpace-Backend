package com.creavispace.project.aop;

import com.creavispace.project.domain.member.service.MemberService;
import com.creavispace.project.global.exception.CreaviCodeException;
import com.creavispace.project.global.exception.GlobalErrorCode;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
@AllArgsConstructor
public class MemberServiceAspect {
    private MemberService memberService;
    @Before("execution(* com.creavispace.project.domain.member.controller.MemberController.readMember*(..))")
    public void beforeReadMember(JoinPoint joinPoint) {
        // REST API 호출 전에 수행할 작업을 구현
        log.info("AOP 실행 = {}", joinPoint.getSignature());
        String memberId = joinPoint.getArgs()[0].toString();
        log.info("memberId = {}", memberId);
        if (!memberService.existMemberId(memberId)) {
            throw new CreaviCodeException(GlobalErrorCode.MEMBER_NOT_FOUND);
        }
    }
}
