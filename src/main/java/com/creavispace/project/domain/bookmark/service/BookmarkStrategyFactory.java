package com.creavispace.project.domain.bookmark.service;

import com.creavispace.project.common.dto.type.PostType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BookmarkStrategyFactory {

    private final ProjectBookmarkStrategy projectBookmarkStrategy;
    private final RecruitBookmarkStrategy recruitBookmarkStrategy;
    private final CommunityBookmarkStrategy communityBookmarkStrategy;

    @Autowired
    public BookmarkStrategyFactory(ProjectBookmarkStrategy projectBookmarkStrategy, RecruitBookmarkStrategy recruitBookmarkStrategy, CommunityBookmarkStrategy communityBookmarkStrategy){
        this.projectBookmarkStrategy = projectBookmarkStrategy;
        this.recruitBookmarkStrategy = recruitBookmarkStrategy;
        this.communityBookmarkStrategy = communityBookmarkStrategy;
    }

    public BookmarkStrategy getStrategy(PostType type){
        return switch (type) {
            case PROJECT -> projectBookmarkStrategy;
            case RECRUIT -> recruitBookmarkStrategy;
            case COMMUNITY -> communityBookmarkStrategy;
            default -> throw new IllegalArgumentException("bookmark strategy type("+type+")이 존재하지 않습니다.");
        };
    }
}
