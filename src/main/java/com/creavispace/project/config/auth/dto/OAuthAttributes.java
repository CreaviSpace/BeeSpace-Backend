package com.creavispace.project.config.auth.dto;

import com.creavispace.project.domain.member.Role;
import com.creavispace.project.domain.member.dto.request.MemberSaveRequestDto;
import com.creavispace.project.domain.member.entity.Member;
import com.creavispace.project.domain.member.service.MemberService;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;

@Getter
public class OAuthAttributes {
    private Map<String, Object> attributes;
    private String nameAttributeKey;
    private String name;
    private String nickName;
    private String email;
    private String loginId;
    private String loginType;

    @Builder
    public OAuthAttributes(Map<String, Object> attributes, String nameAttributeKey, String name, String email,
                           String loginType, String loginId, String nickName) {
        this.attributes = attributes;
        this.nameAttributeKey = nameAttributeKey;
        this.name = name;
        this.email = email;
        this.loginType = loginType;
        this.loginId = loginId;
        this.nickName = nickName;
    }

    public static OAuthAttributes of(String registrationId, String memberNameAttributeName,
                                     Map<String, Object> attributes) {
        if ("google".equals(registrationId)) {
            return ofGoogle(memberNameAttributeName, attributes, registrationId);
        }
        return ofNaver("id", attributes, registrationId);
    }

    private static OAuthAttributes ofNaver(String userNameAttributeName, Map<String, Object> attributes,
                                           String registrationId) {
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");
        System.out.println("------------------------네이버 로그인----------------------------");
        System.out.println("response = " + response);
        return OAuthAttributes.builder()
                .name((String) response.get("name"))
                .email((String) response.get("email"))
                .loginId((String) response.get("id"))
                .nickName((String) response.get("nickname"))
                .loginType(registrationId)
                .attributes(response)
                .nameAttributeKey(userNameAttributeName)
                .build();
    }

    private static OAuthAttributes ofGoogle(String memberNameAttributeName, Map<String, Object> attributes,
                                            String registrationId) {
        System.out.println("------------------------구글 로그인----------------------------");
        System.out.println("attributes = " + attributes);
        return OAuthAttributes.builder()
                .name((String) attributes.get("family_name") + attributes.get("given_name"))
                .email((String) attributes.get("email"))
                .loginId((String) attributes.get("sub"))
                .nickName((String) attributes.get("name"))
                .loginType(registrationId)
                .attributes(attributes)
                .nameAttributeKey(memberNameAttributeName)
                .build();
    }

    public Member toEntity(MemberService memberService) {
        MemberSaveRequestDto dto = MemberSaveRequestDto.builder()
                .memberEmail(email)
                .memberName(name)
                .loginId(loginId)
                .loginType(loginType)
                .memberNickname(nickName)
                .memberName(name)
                .role(Role.MEMBER).build();

        return new Member(dto, memberService);
    }
}
