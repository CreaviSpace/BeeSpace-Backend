package com.creavispace.project.domain.hashTag.service;

import java.util.List;

import com.creavispace.project.common.dto.response.SuccessResponseDto;

public interface HashTagService {
    public SuccessResponseDto<List<String>> readPopularHashTagList();
}
