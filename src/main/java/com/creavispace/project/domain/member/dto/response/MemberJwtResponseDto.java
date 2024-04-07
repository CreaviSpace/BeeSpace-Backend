package com.creavispace.project.domain.member.dto.response;


public record MemberJwtResponseDto(String memberEmail, String loginType, String memberIdTag) {
}
