package com.creavispace.project.domain.member.entity;

import com.creavispace.project.config.auth.dto.OAuthAttributes;
import com.creavispace.project.domain.common.entity.BaseTimeEntity;
import com.creavispace.project.domain.member.Role;
import com.creavispace.project.domain.member.dto.request.MemberSaveRequestDto;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

//이메일
//비밀번호
//사용자 이름
//사용자 닉네임
//생성일
//수정일
//계정 탈퇴
//계정 활성화
//권한
//이메일 인증
//자기소개
@Entity
@Builder
@ToString
@Getter
@Setter
@Table(name = "member")
@NoArgsConstructor
@AllArgsConstructor
public class Member extends BaseTimeEntity {
    @Id
    @Column(name = "member_id")
    //회원 관련 정보와 로그인 정보는 분리
    private String id;

    @Column(nullable = false)
    @JsonBackReference
    private String loginId;

    @Column(nullable = false, unique = false)
    @JsonBackReference
    private String memberEmail;

    @Column(nullable = false)
    @JsonBackReference
    private String memberName;

    @Column(nullable = false, unique = true)
    private String memberNickname;

    @JsonBackReference
    private String profileUrl;

    @Column(nullable = false)
    @JsonBackReference
    private String loginType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @JsonBackReference
    private Role role;

    @JsonBackReference
    private String memberIntroduce;

    @JsonBackReference
    private boolean expired;

    @JsonBackReference
    private boolean enabled;

    @JsonBackReference
    private String idTag;
    @JsonBackReference
    private String memberPosition;
    @JsonBackReference
    private Integer memberCareer;
    @JsonBackReference
    private List<String> interestedStack;

    public Member(MemberSaveRequestDto memberSaveRequestDto) {
        this.memberEmail = memberSaveRequestDto.getMemberEmail();
        this.memberName = memberSaveRequestDto.getMemberName();
        this.loginId = memberSaveRequestDto.getLoginId();
        this.loginType = memberSaveRequestDto.getLoginType();
        this.role = memberSaveRequestDto.getRole();
        this.memberNickname = memberSaveRequestDto.getMemberNickname();
        expired = false;
        enabled = false;
    }

    public Member(MemberSaveRequestDto dto, OAuthAttributes oAuthAttributes) {
        this.idTag = UUID.randomUUID().toString().substring(0,7);
        this.memberEmail = dto.getMemberEmail();
        this.memberName = dto.getMemberName();
        this.loginId = dto.getLoginId();
        this.loginType = dto.getLoginType();
        this.role = dto.getRole();
        this.memberNickname = dto.getMemberNickname();
        this.interestedStack = new ArrayList<>();
        this.memberCareer = 0;
        this.memberPosition = "";
        this.profileUrl = "";
        this.memberIntroduce= "";
        expired = false;
        enabled = false;
    }

    public String getRoleKey() {
        return this.role.getKey();
    }
}
