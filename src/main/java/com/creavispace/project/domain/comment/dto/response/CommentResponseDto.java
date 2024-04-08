package com.creavispace.project.domain.comment.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponseDto {
    private Long id;
    private String memberId;
    private String memberNickName;
    private String memberProfileUrl;
    private LocalDateTime modifiedDate;
    private String content;
    private String contentsTitle;
}
