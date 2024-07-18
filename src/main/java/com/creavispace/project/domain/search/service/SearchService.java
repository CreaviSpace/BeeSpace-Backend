package com.creavispace.project.domain.search.service;

import com.creavispace.project.common.dto.response.SuccessResponseDto;
import com.creavispace.project.common.dto.type.SearchType;
import com.creavispace.project.domain.search.dto.response.SearchListReadResponseDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SearchService {
    public SuccessResponseDto<List<SearchListReadResponseDto>> readSearchList(Pageable pageable, String text, SearchType postType);
}
