package com.creavispace.project.domain.admin.dto;

import com.creavispace.project.domain.member.Role;
import com.creavispace.project.domain.member.entity.Member;
import lombok.Getter;

@Getter
public class MemberListDto {
    private String id;
    private String loginId;
    private String memberEmail;
    private String memberName;
    private String memberNickname;
    private String loginType;
    private Role role;
    private String idTag;
    private String memberPosition;
    private Integer memberCareer;

    public MemberListDto(Member member) {
        this.id = member.getId();
        this.loginId = member.getLoginId();
        this.memberEmail = member.getMemberEmail();
        this.memberName = member.getMemberName();
        this.memberNickname = member.getMemberNickname();
        this.loginType = member.getLoginType();
        this.role = member.getRole();
        this.idTag = member.getIdTag();
        this.memberPosition = member.getMemberPosition();
        this.memberCareer = member.getMemberCareer();
    }

}
