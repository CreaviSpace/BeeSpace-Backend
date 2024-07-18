package com.creavispace.project.domain.search.controller;

import com.creavispace.project.common.dto.response.SuccessResponseDto;
import com.creavispace.project.common.dto.type.SearchType;
import com.creavispace.project.domain.search.dto.response.SearchListReadResponseDto;
import com.creavispace.project.domain.search.service.SearchService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/search")
public class SearchController {
    
    private final SearchService searchService;

    private final static String READ_SEARCH_LIST = "";

    @GetMapping(READ_SEARCH_LIST)
    @Operation(summary = "통합 검색 리스트 조회")
    public ResponseEntity<SuccessResponseDto<List<SearchListReadResponseDto>>> readSearchList(
        @SortDefault(sort = {"id"}, direction = Sort.Direction.DESC) Pageable pageable,
        @RequestParam(value = "text", required = true) String text,
        @RequestParam(value = "searchType", required = false) SearchType searchType
    ){
        return ResponseEntity.ok().body(searchService.readSearchList(pageable, text, searchType));
    }

}
