package com.creavispace.project.domain.auth.oauth2.service;

import com.creavispace.project.domain.auth.oauth2.dto.*;
import com.creavispace.project.domain.member.entity.Member;
import com.creavispace.project.domain.member.entity.Role;
import com.creavispace.project.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class CustomOauth2Service implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    private final MemberRepository memberRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);
        System.out.println(oAuth2User);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        OAuth2Response oAuth2Response = null;
        if(registrationId.equals("naver")){
            oAuth2Response = new NaverResponse(oAuth2User.getAttributes());
        }
        else if (registrationId.equals("google")) {

            oAuth2Response = new GoogleResponse(oAuth2User.getAttributes());
        }
        else if (registrationId.equals("kakao")) {

            oAuth2Response = new KakaoResponse(oAuth2User.getAttributes());
        }
        else {

            return null;
        }
        String providerInfo = oAuth2Response.getProvider()+" "+oAuth2Response.getProviderId();
        Optional<Member> existData = memberRepository.findByProvider(providerInfo);

        if (existData.isEmpty()) {

            String memberId = UUID.randomUUID().toString().substring(0,8);
            while(memberRepository.existsById(memberId)){
                memberId = UUID.randomUUID().toString().substring(0,8);
            }

            Member member = Member.builder()
                    .id(memberId)
                    .provider(providerInfo)
                    .memberEmail(oAuth2Response.getEmail())
                    .memberNickname(oAuth2Response.getName())
                    .role(Role.INCOMPLETE_MEMBER)
                    .build();

            memberRepository.save(member);

            return new CustomOAuth2User(member);
        }
        else {

            Member member = existData.get();

            return new CustomOAuth2User(member);
        }
    }
}
