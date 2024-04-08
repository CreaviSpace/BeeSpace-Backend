package com.creavispace.project.config.auth;

import com.creavispace.project.config.auth.dto.OAuthAttributes;
import com.creavispace.project.domain.member.entity.Member;
import com.creavispace.project.domain.member.service.MemberService;
import jakarta.servlet.http.HttpSession;
import java.util.Collections;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CustomOauth2Service implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    private final MemberService memberService;
    private final HttpSession httpSession;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        System.out.println("-------------------Load User---------------------------");

        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);
        System.out.println(oAuth2User.getAttributes());
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        System.out.println("-------------------로그인 유형 = " + registrationId + " ---------------------------");

        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails()
                .getUserInfoEndpoint().getUserNameAttributeName();

        OAuthAttributes attributes = OAuthAttributes.of(registrationId, userNameAttributeName, oAuth2User.getAttributes());

        Member member = findOrSave(attributes);
        httpSession.setAttribute("jwt", memberService.login(member.getMemberEmail(), member.getLoginType(), member.getId()));
        httpSession.setMaxInactiveInterval(600);


        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(member.getRoleKey())),
                attributes.getAttributes(),
                attributes.getNameAttributeKey());
    }

    private Member findOrSave(OAuthAttributes attributes) {
        System.out.println("-----------------------findOrSave------------------------");
        Optional<Member> memberOptional = memberService
                .findByEmailAndNameAndLoginId(
                        attributes.getEmail(),
                        attributes.getName(),
                        attributes.getLoginId());

        if (memberOptional.isPresent()) {
            System.out.println("-----------------------findOrSaveEnd : 존재하는 아이디------------------------");
            System.out.println("기존 email = " + memberOptional.get());
            return memberOptional.get();
        }

        System.out.println("-----------------------findOrSaveEnd : 새로운 아이디 생성------------------------");
        Member member = memberService.save(attributes.toEntity(memberService));

        System.out.println("새로운 email = " + member.getMemberEmail());
        return member;
    }
}
