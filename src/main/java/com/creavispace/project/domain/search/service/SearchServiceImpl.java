package com.creavispace.project.domain.search.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.creavispace.project.domain.common.dto.response.SuccessResponseDto;
import com.creavispace.project.domain.common.dto.type.PostType;
import com.creavispace.project.domain.common.dto.type.SearchType;
import com.creavispace.project.domain.community.dto.response.CommunityHashTagDto;
import com.creavispace.project.domain.community.dto.response.CommunityResponseDto;
import com.creavispace.project.domain.community.entity.Community;
import com.creavispace.project.domain.community.repository.CommunityRepository;
import com.creavispace.project.domain.project.dto.response.ProjectLinkResponseDto;
import com.creavispace.project.domain.project.dto.response.ProjectListReadResponseDto;
import com.creavispace.project.domain.project.entity.Project;
import com.creavispace.project.domain.project.repository.ProjectRepository;
import com.creavispace.project.domain.recruit.dto.response.RecruitListReadResponseDto;
import com.creavispace.project.domain.recruit.dto.response.RecruitTechStackResponseDto;
import com.creavispace.project.domain.recruit.entity.Recruit;
import com.creavispace.project.domain.recruit.repository.RecruitRepository;
import com.creavispace.project.domain.search.dto.response.SearchListReadResponseDto;
import com.creavispace.project.domain.search.entity.SearchResultSet;
import com.creavispace.project.global.exception.CreaviCodeException;
import com.creavispace.project.global.exception.GlobalErrorCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService{

    private final ProjectRepository projectRepository;
    private final CommunityRepository communityRepository;
    private final RecruitRepository recruitRepository;

    @Override
    public SuccessResponseDto<List<SearchListReadResponseDto>> readSearchList(Integer size, Integer page, String text,
            SearchType searchType) {
        List<SearchListReadResponseDto> data = null;
        Pageable pageRequest = PageRequest.of(page-1, size);
        Page<SearchResultSet> pageable;

        if(searchType == null){
            pageable = projectRepository.findIntegratedSearchData(text, pageRequest);
        }else{
            switch (searchType.name()) {
                case "PROJECT":
                    pageable = projectRepository.findProjectSearchData(text, pageRequest);
                    break;

                case "TEAM":
                    pageable = projectRepository.findProjectCategorySearchData(text, searchType.name() ,pageRequest);
                    break;
                    
                case "INDIVIDUAL":
                    pageable = projectRepository.findProjectCategorySearchData(text, searchType.name() ,pageRequest);
                    break;
                    
                case "RECRUIT":
                    pageable = recruitRepository.findRecruitSearchData(text, pageRequest);
                    break;
                    
                case "PROJECT_RECRUIT":
                    pageable = recruitRepository.findRecruitCategorySearchData(text, searchType.name(), pageRequest);
                    break;
                    
                case "STUDY":
                    pageable = recruitRepository.findRecruitCategorySearchData(text, searchType.name(), pageRequest);
                    break;
                
                case "COMMUNITY":
                    pageable = communityRepository.findCommunitySearchData(text, pageRequest);
                    break;
                    
                case "QNA":
                    pageable = communityRepository.findCommunityCategorySearchData(text, searchType.name(), pageRequest);
                    break;
                    
                case "CHAT":
                    pageable = communityRepository.findCommunityCategorySearchData(text, searchType.name(), pageRequest);
                    break;
                    
                case "CONCERN":
                    pageable = communityRepository.findCommunityCategorySearchData(text, searchType.name(), pageRequest);
                    break;
                    
            
                default:
                    throw new CreaviCodeException(GlobalErrorCode.TYPE_NOT_FOUND);
            }
        }

        if(!pageable.hasContent()) throw new CreaviCodeException(GlobalErrorCode.NOT_SEARCH_CONTENT);
        List<SearchResultSet> searchResults = pageable.getContent();

        data = searchResults.stream()
            .map(searchResult -> {
                Long postId = searchResult.getPostId();
                switch (searchResult.getPostType()) {
                    case "PROJECT":
                        Project project = projectRepository.findByIdAndStatusTrue(postId).orElseThrow(() -> new CreaviCodeException(GlobalErrorCode.PROJECT_NOT_FOUND));
                        return ProjectListReadResponseDto.builder()
                            .id(project.getId())
                            .postType(PostType.PROJECT.name())
                            .category(project.getCategory().name())
                            .title(project.getTitle())
                            .links(project.getLinks().stream()
                                .map(link -> ProjectLinkResponseDto.builder()
                                    .linkType(link.getLinkType())
                                    .url(link.getUrl())
                                    .build())
                                .collect(Collectors.toList()))
                            .thumbnail(project.getThumbnail())
                            .bannerContent(project.getBannerContent())
                            .build();
                    case "RECRUIT":
                        Recruit recruit = recruitRepository.findByIdAndStatusTrue(postId).orElseThrow(()-> new CreaviCodeException(GlobalErrorCode.RECRUIT_NOT_FOUND));
                        return RecruitListReadResponseDto.builder()
                            .id(recruit.getId())
                            .postType(PostType.RECRUIT.name())
                            .category(recruit.getCategory().name())
                            .title(recruit.getTitle())
                            .content(recruit.getContent())
                            .amount(recruit.getAmount())
                            .now(recruit.getPositions().stream()
                                .mapToInt(position -> position.getNow())
                                .sum())
                            .techStacks(recruit.getTechStacks().stream()
                                .map(techstack -> RecruitTechStackResponseDto.builder()
                                    .techStack(techstack.getTechStack().getTechStack())
                                    .iconUrl(techstack.getTechStack().getIconUrl())
                                    .build())
                                .collect(Collectors.toList()))
                            .build();
                    case "COMMUNITY":
                        Community community = communityRepository.findByIdAndStatusTrue(postId).orElseThrow(()-> new CreaviCodeException(GlobalErrorCode.COMMUNITY_NOT_FOUND));
                        return CommunityResponseDto.builder()
                            .id(community.getId())
                            .postType(PostType.COMMUNITY.name())
                            .category(community.getCategory().name())
                            .memberId(community.getMember().getId())
                            .memberNickName(community.getMember().getMemberNickname())
                            .memberProfile(community.getMember().getProfileUrl())
                            .viewCount(community.getViewCount())
                            .createdDate(community.getCreatedDate())
                            .modifiedDate(community.getModifiedDate())
                            .title(community.getTitle())
                            .content(community.getContent())
                            .hashTags(community.getCommunityHashTags().stream()
                                .map(hashTag -> CommunityHashTagDto.builder()
                                    .hashTagId(hashTag.getHashTag().getId())
                                    .hashTag(hashTag.getHashTag().getHashTag())
                                    .build())
                                .collect(Collectors.toList()))
                            .build();
                    default:
                        throw new CreaviCodeException(GlobalErrorCode.TYPE_NOT_FOUND);
                }
            })
            .collect(Collectors.toList());

        log.info("/search/service : readSearchList success data = {}", data);
        // 성공 응답 반환
        return new SuccessResponseDto<>(true, "통합 검색 리스트 조회가 완료되었습니다.", data);
    }

}
