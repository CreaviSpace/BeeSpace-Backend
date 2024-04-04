package com.creavispace.project.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum GlobalErrorCode {
    NOT_FOUND_POST_TYPE(400, "게시글 타입이 존재하지 않습니다."),
    NOT_FOUND_COMMUNITY_CATEGORY(400, "커뮤니티 카테고리가 존재하지 않습니다."),
    NOT_FOUND_ORDERBY(400, "커뮤니티 정렬타입이 존재하지 않습니다."),
    NOT_FOUND_QUESTION_TYPE(400, "피드백 질문 타입이 존재하지 않습니다."),
    NOT_FOUND_PROJECT_CATEGORY(400, "프로젝트 카테고리가 존재하지 않습니다."),
    NOT_FOUND_RECRUIT_CATEGORY(400, "모집 게시글 카테고리가 존재하지 않습니다."),
    NOT_FOUND_REPORT_CATEGORY(400, "신고 카테고리가 존재하지 않습니다."),
    NOT_FOUND_RECRUIT_CONTACTWAY(400, "모집 연락방법이 존재하지 않습니다."),
    NOT_FOUND_RECRUIT_PROCEEDWAY(400, "모집 진행방식이 존재하지 않습니다."),

    MEMBER_NOT_FOUND(400,"해당 회원이 존재하지 않습니다."),
    TECHSTACK_NOT_FOUND(400,"해당 기술스택이 존재하지 않습니다."),
    PROJECT_NOT_FOUND(400,"해당 프로젝트가 존재하지 않습니다."),
    NOT_PERMISSMISSION(401,"권한이 없습니다."),
    NOT_PROJECT_CONTENT(204, "프로젝트 게시글 조회 결과가 0건입니다."),
    COMMENT_NOT_FOUND(400, "해당 댓글이 존재하지 않습니다."),
    S3_SERVER_NOT_FOUND(500, "이미지 저장에 실패했습니다."),
    RECRUIT_NOT_FOUND(400,"해당 모집 게시글이 존재하지 않습니다."),
    NOT_RECRUIT_CONTENT(204,"모집 게시글 조회 결과가 0건입니다."),
    COMMUNITY_NOT_FOUND(400, "해당 커뮤니티 게시글이 존재하지 않습니다."),
    NOT_COMMUNITY_CONTENT(204, "커뮤니티 게시글 조회 결과가 0건입니다."),
    LIKE_COUNT_NOT_FOUND(400, "좋아요 수 조회에 실패하였습니다."),
    TYPE_NOT_FOUND(400,"게시글 타입을 찾을수 없습니다."),
    NOT_SEARCH_CONTENT(204, "검색 결과가 0건입니다."),
    ORDERBY_NOT_FOUND(400,"정렬 타입을 찾을수 없습니다."),
    RECRUIT_END_IS_BEFORE_NOW(400, "모집 마감일이 오늘보다 이전 날짜 입니다."),
    FEEDBACK_QUESTION_NOT_FOUND(400,"해당 피드백 질문이 존재하지 않습니다."),
    CHOICE_ITEM_NOT_FOUND(400, "해당 선택지가 존재하지 않습니다."),
    ALREADY_FEEDBACK_ANSWER(400, "이미 피드백한 프로젝트입니다."),
    QUESTION_TYPE_NOT_FOUND(400, "피드백 일부의 QuestionType을 찾을 수 없습니다."),
    NOT_QUESTION_ANALYSIS_CONTENT(204, "해당 프로젝트의 피드백 분석할 질문이 0건입니다."),
    NOT_FEEDBACK_ANSWER_CONTENT(400, "피드백 답변이 존재하지 않습니다."),
    NOT_FOUND_ALARM(400,"해당 알림이 존재하지 않습니다." );

    private final Integer code;
    private final String message;
}
