package com.creavispace.project.domain.comment.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponseDto {
    private Long id;
    private String memberId;
    private String memberNickName;
    private String memberProfileUrl;
    private LocalDateTime modifiedDate;
    private String content;
}
