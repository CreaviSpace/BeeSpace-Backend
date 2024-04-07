package com.creavispace.project.config.auth;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.creavispace.project.config.auth.dto.LoginResponseDto;
import com.creavispace.project.config.auth.dto.NaverTokenResponseDto;
import com.creavispace.project.config.auth.dto.NaverUserResponseDto;
import com.creavispace.project.config.auth.dto.NaverUserResponseDto.NaverUserDetail;
import com.creavispace.project.config.auth.utils.JwtUtil;
import com.creavispace.project.domain.common.dto.response.SuccessResponseDto;
import com.creavispace.project.domain.member.Role;
import com.creavispace.project.domain.member.entity.Member;
import com.creavispace.project.domain.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LoginServiceImpl implements LoginService {

    @Value("${spring.security.oauth2.client.registration.naver.client-id}")
    private String NAVER_CLIENT_ID;
    @Value("${spring.security.oauth2.client.registration.naver.client-secret}")
    private String NAVER_CLIENT_SECRET;
    @Value("${jwt.secret}")
    private String JWT_SECRET;

    private final String GRANT_TYPE = "authorization_code";
    private final String NAVER_TOKEN_URL = "https://nid.naver.com/oauth2.0/token";
    private final String NAVER_PROFILE_API_URL = "https://openapi.naver.com/v1/nid/me";

    private final MemberRepository memberRepository;

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public SuccessResponseDto<LoginResponseDto> naverLogin(String code, String state) {
        
        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromHttpUrl(NAVER_TOKEN_URL)
                .queryParam("grant_type", GRANT_TYPE).queryParam("client_id", NAVER_CLIENT_ID)
                .queryParam("state", state).queryParam("code", code)
                .queryParam("client_secret", NAVER_CLIENT_SECRET);

        ResponseEntity<NaverTokenResponseDto> tokenResponseEntity = restTemplate.exchange(uriComponentsBuilder.toUriString(),
                HttpMethod.GET, null, NaverTokenResponseDto.class);
        final String accessToken = tokenResponseEntity.getBody().getAccessToken();

        System.out.println("accessToken = " + accessToken);
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(headers);

        ResponseEntity<NaverUserResponseDto> userInfoResponseEntity = restTemplate.exchange(NAVER_PROFILE_API_URL, HttpMethod.GET,request, NaverUserResponseDto.class);

        NaverUserDetail userInfo = userInfoResponseEntity.getBody().getNaverUserDetail();
        System.out.println("userInfo.getEmail() = " + userInfo.getEmail());
        System.out.println("userInfo.getId() = " + userInfo.getId());
        System.out.println("userInfo.getName() = " + userInfo.getName());
        System.out.println("userInfo.getNickname() = " + userInfo.getNickname());

        Optional<Member> memberOptional = memberRepository.findByLoginId(userInfo.getId());
        
        Member member;
        
        if (memberOptional.isPresent()) {
            System.out.println("이미 가입된 아이디입니다.");
            member = memberOptional.get();
        }else{
            System.out.println("회원가입에 성공하였습니다.");
            member = Member.builder()
                .loginId(userInfo.getId())
                .memberEmail(userInfo.getEmail())
                .memberName(userInfo.getName())
                .memberNickname(userInfo.getNickname() == null ? "nickname"+UUID.randomUUID().toString() : userInfo.getNickname())
                .loginType("Naver")
                .role(Role.MEMBER)
                .build();
            memberRepository.save(member);
        }
        
        String token = JwtUtil.createJwt(member.getMemberEmail(), member.getLoginType(), member.getIdTag(), JWT_SECRET, 1000 * 60 * 60L);

        LoginResponseDto responseDto = LoginResponseDto.builder()
            .token(token)
            .expirated(1000 * 60 * 60L)
            .build();

        return new SuccessResponseDto<>(true, "네이버로그인에 성공하였습니다.", responseDto);
    }
    
}
