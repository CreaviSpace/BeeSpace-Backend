package com.creavispace.project.domain.mypage.dto.response;

import com.creavispace.project.common.dto.response.SuccessResponseDto;
import com.creavispace.project.domain.project.dto.response.ProjectListReadResponseDto;
import java.util.List;
import lombok.Getter;

@Getter
public class MyPageContentsResponseDto {
    private final SuccessResponseDto<List<ProjectListReadResponseDto>> myContents;

    public MyPageContentsResponseDto(SuccessResponseDto<List<ProjectListReadResponseDto>> myContents) {
        this.myContents = myContents;
    }

    public MyPageContentsResponseDto() {
        this.myContents = null;
    }
}

