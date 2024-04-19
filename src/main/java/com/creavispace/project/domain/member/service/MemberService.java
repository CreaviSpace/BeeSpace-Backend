package com.creavispace.project.domain.member.service;

import com.creavispace.project.domain.member.dto.response.MemberResponseDto;
import com.creavispace.project.domain.member.entity.Member;
import com.creavispace.project.domain.mypage.dto.request.MyPageModifyRequestDto;

import java.util.List;
import java.util.Optional;

public interface MemberService {
    Member save(Member member);

    Member update(Member member, MyPageModifyRequestDto updateParam);

    Optional<Member> findByEmail(String email);

    Member findById(String memberId);

    Optional<Member> findByEmailAndNameAndLoginId(String email, String name, String loginId);

    boolean emailExsists(String memberEmail);

    List<Member> findAllMembers(Integer size, Integer page, String sortType);

    String login(String memberEmail, String loginType, String memberId);

    Optional<Member> findByEmailAndLoginTypeAndMemberId(String memberEmail, String loginType, String memberId);

    List<MemberResponseDto> findByMemberNicknameOrIdContaining(String search);
    Optional<Member> findByLoginId(String loginId);

    void expireMember(String jwt);

    boolean existMemberId(String id);

    String createId();

    Optional<Member> findByLoginIdAndLoginType(String loginId, String loginType);
}