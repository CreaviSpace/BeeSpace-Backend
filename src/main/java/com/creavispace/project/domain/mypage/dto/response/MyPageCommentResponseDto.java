package com.creavispace.project.domain.mypage.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class MyPageCommentResponseDto {
    private Long postId;
    private String postTitle;
    private LocalDateTime postModifiedDate;
    private String comment;

}
