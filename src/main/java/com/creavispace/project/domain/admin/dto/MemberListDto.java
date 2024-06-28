package com.creavispace.project.domain.admin.dto;

import com.creavispace.project.domain.member.Role;
import com.creavispace.project.domain.member.entity.Member;
import lombok.Getter;

@Getter
public class MemberListDto {
    private final String id;
    private final String loginId;
    private final String memberEmail;
    private final String memberNickname;
    private final String loginType;
    private final Role role;
    private final String memberPosition;
    private final Integer memberCareer;

    public MemberListDto(Member member) {
        this.id = member.getId();
        this.loginId = member.getLoginId();
        this.memberEmail = member.getMemberEmail();
        this.memberNickname = member.getMemberNickname();
        this.loginType = member.getLoginType();
        this.role = member.getRole();
        this.memberPosition = member.getMemberPosition();
        this.memberCareer = member.getMemberCareer();
    }

}
