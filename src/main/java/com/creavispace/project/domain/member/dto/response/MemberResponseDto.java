package com.creavispace.project.domain.member.dto.response;

import com.creavispace.project.domain.member.entity.Member;
import com.creavispace.project.domain.techStack.dto.response.TechStackListReadResponseDto;
import com.creavispace.project.domain.techStack.entity.TechStack;
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
    private final String memberId;
    private final String memberEmail;
    private final String profileUrl;
    private final String memberNickname;
    private final Integer memberCareer;
    private final String memberPosition;
    private final String memberIntroduce;
    private final List<TechStackListReadResponseDto> memberInterestedStack;
    private final String message;
    private final boolean enabled;

    public MemberResponseDto(Member member) {
        this.memberId = member.getId();
        this.memberEmail = member.getMemberEmail();
        this.profileUrl = member.getProfileUrl();
        this.memberNickname = member.getMemberNickname();
        this.memberCareer = member.getMemberCareer();
        this.memberPosition = member.getMemberPosition();
        memberIntroduce = null;
        memberInterestedStack = null;
        this.enabled = member.isEnabled();
        this.message = "데이터 조회 성공";
    }
    public MemberResponseDto(Member member, List<TechStack> techStacks) {
        this.memberId = member.getId();
        this.memberEmail = member.getMemberEmail();
        this.profileUrl = member.getProfileUrl();
        this.memberNickname = member.getMemberNickname();
        this.memberCareer = member.getMemberCareer();
        this.memberPosition = member.getMemberPosition();
        this.memberIntroduce = member.getMemberIntroduce();
        this.memberInterestedStack = convertStack(techStacks);
        this.enabled = member.isEnabled();
        this.message = "데이터 조회 성공";
    }

    private static List<TechStackListReadResponseDto> convertStack(List<TechStack> techStacks) {
        return techStacks.stream().map(stack -> new TechStackListReadResponseDto(stack.getTechStack(),
                stack.getIconUrl())).toList();
    }
}
