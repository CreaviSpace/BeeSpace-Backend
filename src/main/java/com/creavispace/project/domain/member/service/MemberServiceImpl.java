package com.creavispace.project.domain.member.service;

import com.creavispace.project.config.auth.utils.JwtUtil;
import com.creavispace.project.domain.member.dto.response.MemberResponseDto;
import com.creavispace.project.domain.member.entity.Member;
import com.creavispace.project.domain.member.repository.MemberRepository;
import com.creavispace.project.domain.mypage.dto.request.MyPageModifyRequestDto;
import com.creavispace.project.global.exception.CreaviCodeException;
import com.creavispace.project.global.exception.GlobalErrorCode;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;

    @Value("${jwt.secret}")
    private String jwtSecret;
    @Transactional
    @Override
    public Member save(Member member) {
        return memberRepository.save(member);
    }

    @Transactional
    @Override
    public Member update(Member member, MyPageModifyRequestDto updateParam) {
        member.setMemberNickname(updateParam.nickName());
        member.setMemberIntroduce(updateParam.introduce());
        member.setMemberPosition(updateParam.position());
        member.setMemberCareer(updateParam.career());
        member.setInterestedStack(updateParam.interestedStack());
        member.setProfileUrl(updateParam.profileUrl());

        if (!member.isEnabled()) {
            member.setEnabled(true);
        }
        return memberRepository.save(member);
    }

    @Override
    public Optional<Member> findByEmail(String email) {
        return memberRepository.findByMemberEmail(email);
    }

    @Override
    public Member findById(String memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(() ->new CreaviCodeException(GlobalErrorCode.MEMBER_NOT_FOUND));
        return member;
    }

    @Override
    public Optional<Member> findByEmailAndNameAndLoginId(String email, String name, String loginId) {
        return memberRepository.findByMemberEmailAndMemberNameAndLoginId(email, name, loginId);
    }

    @Override
    public boolean emailExsists(String memberEmail) {
        return memberRepository.existsByMemberEmail(memberEmail);
    }

    @Override
    public List<Member> findAllMembers(Integer size, Integer page, String sortType) {
        Pageable pageRequest = PageRequest.of(page - 1, size);
        List<Member> memberList = memberRepository.findBy(pageRequest);
        System.out.println("memberList = " + memberList);
        return memberList;
    }

    @Override
    public String login(String memberEmail, String loginType, String memberId) {
        String jwt = JwtUtil.createJwt(memberEmail, loginType, memberId, jwtSecret, 1000 * 60 * 60L);

        return jwt;
    }

    @Override
    public Optional<Member> findByEmailAndLoginTypeAndMemberId(String memberEmail, String loginType, String memberId) {
        return memberRepository.findByMemberEmailAndLoginTypeAndId(memberEmail, loginType, memberId);
    }

    @Override
    public List<MemberResponseDto> findByMemberNicknameOrIdContaining(String search) {
        List<Member> searchData = memberRepository.findByMemberNicknameOrIdTagContaining(search);
        return searchData.stream().map(MemberResponseDto::new).collect(Collectors.toList());
    }
    @Override
    public Optional<Member> findByLoginId(String loginId) {
        return memberRepository.findByLoginId(loginId);
    }

    @Override
    public void expireMember(String jwt) {
        String memberId = JwtUtil.getUserInfo(jwt, jwtSecret).memberId();
        Member member = findMember(memberId);
        member.setExpired(true);
        member.setEnabled(false);
        memberRepository.save(member);
    }

    @Override
    public boolean existMemberId(String id) {
        return memberRepository.existsById(id);
    }

    @Override
    public String createId() {
        String createdId = UUID.randomUUID().toString().substring(0, 8);

        while (existMemberId(createdId)) {
            createdId = UUID.randomUUID().toString().substring(0, 8);
        }
        return createdId;
    }

    private Member findMember(String memberId) {
        Optional<Member> memberOptional = memberRepository.findById(memberId);
        if (memberOptional.isPresent()) {
            return memberOptional.get();
        }
        throw new IllegalStateException();
    }
}
