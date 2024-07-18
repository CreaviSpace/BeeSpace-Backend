package com.creavispace.project.domain.search.service;

import com.creavispace.project.common.dto.response.SuccessResponseDto;
import com.creavispace.project.common.dto.type.PostType;
import com.creavispace.project.common.dto.type.SearchType;
import com.creavispace.project.common.exception.CreaviCodeException;
import com.creavispace.project.common.post.entity.Post;
import com.creavispace.project.common.post.repository.PostRepository;
import com.creavispace.project.domain.community.entity.CommunityCategory;
import com.creavispace.project.domain.community.repository.CommunityRepository;
import com.creavispace.project.domain.project.entity.Project;
import com.creavispace.project.domain.project.repository.ProjectRepository;
import com.creavispace.project.domain.recruit.entity.Recruit;
import com.creavispace.project.domain.recruit.repository.RecruitRepository;
import com.creavispace.project.domain.search.dto.response.SearchListReadResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService{

    private final ProjectRepository projectRepository;
    private final CommunityRepository communityRepository;
    private final RecruitRepository recruitRepository;
    private final PostRepository postRepository;

    @Transactional(readOnly = true)
    @Override
    public SuccessResponseDto<List<SearchListReadResponseDto>> readSearchList(Pageable pageable, String text,
            SearchType searchType) {
        // 검색 조건에 맞는 Post 엔티티 조회
        Page<Post> postPage;
        if(searchType == null){
            // 모든 게시글 조회
            postPage = postRepository.findBySearch(text, pageable);
        } else {
            // 검색 카테고리
            String category = searchType.getCategory();
            
            if (searchType.getPostType().equals(PostType.PROJECT)) {
                // 프로젝트 카테고리
                Project.Category projectCategory = Project.Category.valueOf(category);
                // 검색 조건에 따른 프로젝트 조회(text,category)
                postPage = projectRepository.findBySearch(text, projectCategory, pageable);
                
            } else if (searchType.getPostType().equals(PostType.RECRUIT)) {
                // 모집 카테고리
                Recruit.Category recruitCategory = Recruit.Category.valueOf(category);
                // 검색 조건에 따른 모집 조회(text,category)
                postPage = recruitRepository.findBySearch(text, recruitCategory, pageable);
                
            } else if (searchType.getPostType().equals(PostType.COMMUNITY)) {
                // 커뮤니티 카테고리
                CommunityCategory communityCategory = CommunityCategory.valueOf(category);
                // 검색 조건에 따른 커뮤니티 조회(text,category)
                postPage = communityRepository.findBySearch(text, communityCategory, pageable);
                
            } else {
                throw new IllegalArgumentException("일치하는 searchType(" + searchType +")이 존재하지 않습니다.");
            }
        }

        // 결과값이 없으면 204 반환
        if(!postPage.hasContent()) throw new CreaviCodeException("",204);

        // 검색 결과 toDto
        List<SearchListReadResponseDto> data = postPage.getContent().stream()
                .map(post -> SearchListReadResponseDto.builder()
                        .id(post.getId())
                        .postType(post.getPostType())
                        .title(post.getTitle())
                        .thumbnail(post.getThumbnail())
                        .bannerContent(post.getBannerContent())
                        .createdDate(post.getCreatedDate())
                        .build())
                .toList();

        // 성공 응답 반환
        return new SuccessResponseDto<>(true, "통합 검색 리스트 조회가 완료되었습니다.", data);
    }

}
