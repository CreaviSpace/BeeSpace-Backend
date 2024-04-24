package com.creavispace.project.domain.mypage.dto.request;

import com.creavispace.project.domain.techStack.dto.response.TechStackListReadResponseDto;
import java.util.List;
import lombok.Getter;


/*
 * 사용자 닉네임
 * 자기소개
 * 직무
 * 경력
 * 관심 스택
 * */
public record MyPageModifyRequestDto(String nickName, String introduce, String position, Integer career,
                                     List<TechStackListReadResponseDto> interestedStack, String profileUrl) {

}
