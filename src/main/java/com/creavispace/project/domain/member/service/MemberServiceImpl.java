package com.creavispace.project.domain.member.service;

import com.creavispace.project.common.dto.response.SuccessResponseDto;
import com.creavispace.project.domain.auth.jwt.repository.RefreshTokenRepository;
import com.creavispace.project.domain.member.dto.response.MemberResponseDto;
import com.creavispace.project.domain.member.dto.response.SearchMemberResponseDto;
import com.creavispace.project.domain.member.entity.Member;
import com.creavispace.project.domain.member.entity.Role;
import com.creavispace.project.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    @Transactional
    public void withdrawn(String memberId) {
        // 맴버 엔티티 조회
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new NoSuchElementException("로그인 회원 아이디가 존재하지 않습니다."));

        // 맴버 Role update(탈퇴한 회원)
        member.changeRole(Role.WITHDRAWN);

        // 리프레시 토큰 삭제
        refreshTokenRepository.deleteByMemberId(memberId);
    }

    @Override
    public SuccessResponseDto<MemberResponseDto> readMember(String memberId) {
        // 맴버 엔티티 조회
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new NoSuchElementException("회원 id(" + memberId + ")가 존재하지 않습니다."));

        // 조회 결과 toDto
        MemberResponseDto data = MemberResponseDto.builder()
                .profileUrl(member.getProfileUrl())
                .career(member.getMemberCareer())
                .email(member.getMemberEmail())
                .nickname(member.getMemberNickname())
                .memberId(member.getId())
                .interestedStacks(member.getInterestedStack().stream()
                        .map(interestTechStack -> interestTechStack.getTechStack().getTechStack()) // todo : N+1 확인하기
                        .toList())
                .build();

        // 성공 응답 반환
        return new SuccessResponseDto<>(true, "회원 프로필 정보 조회가 완료되었습니다.", data);
    }

    @Override
    public SuccessResponseDto<List<SearchMemberResponseDto>> searchMember(String text) {
        // 맴버 엔티티 조회
        List<Member> members = memberRepository.findSearchByIdOrMemberNickname(text);

        // 조회 결과 toDto
        List<SearchMemberResponseDto> data = members.stream()
                .map(member -> SearchMemberResponseDto.builder()
                        .nickname(member.getMemberNickname())
                        .memberId(member.getId())
                        .build())
                .toList();

        // 성공 응답 반환
        return new SuccessResponseDto<>(true, "회원 ID 검색이 완료되었습니다.", data);
    }

}
