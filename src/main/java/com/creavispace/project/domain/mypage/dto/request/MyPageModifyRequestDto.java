package com.creavispace.project.domain.mypage.dto.request;

import com.creavispace.project.domain.mypage.dto.response.MyPageTechStackRequestDto;
import java.util.List;


/*
 * 사용자 닉네임
 * 자기소개
 * 직무
 * 경력
 * 관심 스택
 * */
public record MyPageModifyRequestDto(String nickName, String introduce, String position, Integer career,
                                     List<MyPageTechStackRequestDto> interestedStack, String profileUrl) {

}
