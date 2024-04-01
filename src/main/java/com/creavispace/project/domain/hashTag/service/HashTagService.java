package com.creavispace.project.domain.hashTag.service;

import java.util.List;

import com.creavispace.project.domain.common.dto.response.SuccessResponseDto;
import com.creavispace.project.domain.hashTag.dto.response.PopularHashTagReadResponseDto;

public interface HashTagService {
    public SuccessResponseDto<List<PopularHashTagReadResponseDto>> readPopularHashTagList();
}
