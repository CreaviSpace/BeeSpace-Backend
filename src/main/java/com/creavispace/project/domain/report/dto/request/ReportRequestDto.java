package com.creavispace.project.domain.report.dto.request;

import com.creavispace.project.common.dto.type.PostType;
import com.creavispace.project.common.dto.type.ReportCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportRequestDto {
    private Long postId;
    private PostType postType;
    private ReportCategory category;
    private String content;
}
