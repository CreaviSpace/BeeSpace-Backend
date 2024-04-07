package com.creavispace.project.domain.member.service;

import com.creavispace.project.domain.member.dto.response.MemberResponseDto;
import com.creavispace.project.domain.member.entity.Member;
import com.creavispace.project.domain.mypage.dto.request.MyPageModifyRequestDto;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;

public interface MemberService {
    Member save(Member member);

    Member update(Member member, MyPageModifyRequestDto updateParam);

    Optional<Member> findByEmail(String email);

    MemberResponseDto findById(Long memberId);

    Optional<Member> findByEmailAndNameAndLoginId(String email, String name, String loginId);

    boolean emailExsists(String memberEmail);

    Page<Member> findAllMembers(Integer size, Integer page, String sortType);

    String login(String memberEmail, String loginType, String memberIdTag);

    Optional<Member> findByEmailAndLoginTypeAndMemberId(String memberEmail, String loginType, Long memberId);

    List<MemberResponseDto> findByMemberNicknameOrIdTagContaining(String search);
    Optional<Member> findByLoginId(String loginId);

    void expireMember(String jwt);

    Optional<Member> findByMemberIdTag(String memberIdTag);
}