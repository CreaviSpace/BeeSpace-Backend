package com.creavispace.project.domain.search.service;

import java.util.List;

import com.creavispace.project.domain.common.dto.response.SuccessResponseDto;
import com.creavispace.project.domain.search.dto.response.SearchListReadResponseDto;

public interface SearchService {
    public SuccessResponseDto<List<SearchListReadResponseDto>> readSearchList(Integer size, Integer page, String text, String postType);
}
