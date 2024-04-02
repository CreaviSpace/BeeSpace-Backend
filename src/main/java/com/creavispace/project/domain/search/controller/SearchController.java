package com.creavispace.project.domain.search.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.creavispace.project.domain.common.dto.response.SuccessResponseDto;
import com.creavispace.project.domain.common.dto.type.PostType;
import com.creavispace.project.domain.search.constant.SwaggerExample;
import com.creavispace.project.domain.search.dto.response.SearchListReadResponseDto;
import com.creavispace.project.domain.search.service.SearchService;
import com.creavispace.project.global.exception.GlobalErrorCode;
import com.creavispace.project.global.util.CustomValueOf;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/search")
public class SearchController {
    
    private final SearchService searchService;

    private final static String READ_SEARCH_LIST = "";

    @GetMapping(READ_SEARCH_LIST)
    @Operation(
        summary = "통합 검색 리스트 조회",
        responses = {
            @ApiResponse(responseCode = "200", content = {@Content(mediaType ="application/json", schema = @Schema(example = SwaggerExample.READ_SEARCH_LIST))})
        }
    )
    public ResponseEntity<SuccessResponseDto<List<SearchListReadResponseDto>>> readSearchList(
        @RequestParam(value = "size", required = false, defaultValue = "6") Integer size,
        @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
        @RequestParam(value = "text", required = true) String text,
        @RequestParam(value = "postType", required = false) String postType
    ){
        log.info("/search/controller : 통합 검색 리스트 조회");
        PostType postTypeEnum = null;
        if(postType != null){
            postTypeEnum = CustomValueOf.valueOf(PostType.class, postType, GlobalErrorCode.NOT_FOUND_POST_TYPE);
        }
        return ResponseEntity.ok().body(searchService.readSearchList(size, page, text, postTypeEnum));
    }

}
