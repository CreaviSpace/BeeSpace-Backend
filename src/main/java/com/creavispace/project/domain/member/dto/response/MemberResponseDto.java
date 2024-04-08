package com.creavispace.project.domain.member.dto.response;

import com.creavispace.project.domain.member.entity.Member;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

/**
 * 사용자가 조회 할 수 있는 데이터
 * 이메일
 * 비밀번호
 * 닉네임
 * 자기소개
 * */
@Getter
@ToString
public class MemberResponseDto {
    private String memberId;
    private String profileUrl;
    private String memberNickname;
    private String idTag;
    private Integer memberCareer;
    private String memberPosition;
    private String memberIntroduce;
    private List<String> memberInterestedStack;

    public MemberResponseDto(Member member) {
        this.memberId = member.getId();
        this.profileUrl = member.getProfileUrl();
        this.memberNickname = member.getMemberNickname();
        this.idTag = member.getIdTag();
        this.memberCareer = member.getMemberCareer();
        this.memberPosition = member.getMemberPosition();
        this.memberIntroduce = member.getMemberIntroduce();
        this.memberInterestedStack = member.getInterestedStack();
    }
}
