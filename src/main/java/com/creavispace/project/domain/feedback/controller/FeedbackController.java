package com.creavispace.project.domain.feedback.controller;

import com.creavispace.project.domain.alarm.service.AlarmService;
import com.creavispace.project.common.dto.response.SuccessResponseDto;
import com.creavispace.project.common.dto.type.PostType;
import com.creavispace.project.domain.feedback.dto.request.FeedbackAnswerCreateRequestDto;
import com.creavispace.project.domain.feedback.dto.request.FeedbackQuestionCreateRequestDto;
import com.creavispace.project.domain.feedback.dto.request.FeedbackQuestionModifyRequestDto;
import com.creavispace.project.domain.feedback.dto.response.FeedbackAnalysisResponseDto;
import com.creavispace.project.domain.feedback.dto.response.FeedbackAnswerCreateResponseDto;
import com.creavispace.project.domain.feedback.dto.response.FeedbackQuestionResponseDto;
import com.creavispace.project.domain.feedback.service.FeedbackService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/feedback")
public class FeedbackController {
    
    private final FeedbackService feedbackService;
    private final AlarmService alarmService;

    private final static String CREATE_FEEDBACK_QUESTION = "/question";
    private final static String MODIFY_FEEDBACK_QUESTION = "/question";
    private final static String READ_FEEDBACK_QUESTION = "/question";
    private final static String CREATE_FEEDBACK_ANSWER = "/answer";
    private final static String ANALYSIS_FEEDBACK = "/analysis";

    @PostMapping(CREATE_FEEDBACK_QUESTION)
    @Operation(summary = "피드백 질문 생성")
    public ResponseEntity<SuccessResponseDto<List<FeedbackQuestionResponseDto>>> createFeedbackQuestion(
        @AuthenticationPrincipal String memberId,
        @RequestParam("projectId") Long projectId, 
        @RequestBody List<FeedbackQuestionCreateRequestDto> requestBody
    ){
        log.info("/feedback/controller : 피드백 질문 생성");
        return ResponseEntity.ok().body(feedbackService.createFeedbackQuestion(memberId, projectId, requestBody));
    }

    @PutMapping(MODIFY_FEEDBACK_QUESTION)
    @Operation(summary = "피드백 질문 수정 - 기존 질문을 수정할순 없고, 질문의 추가 생성/삭제가 가능한 기능")
    public ResponseEntity<SuccessResponseDto<List<FeedbackQuestionResponseDto>>> modifyFeedbackQuestion(
        @AuthenticationPrincipal String memberId,
        @RequestParam("projectId") Long projectId,
        @RequestBody List<FeedbackQuestionModifyRequestDto> requestBody
    ){
        log.info("/feedback/controller : 피드백 질문 수정");
        return ResponseEntity.ok().body(feedbackService.modifyFeedbackQuestion(memberId, projectId, requestBody));
    }

    @GetMapping(READ_FEEDBACK_QUESTION)
    @Operation(summary = "피드백 질문 리스트")
    public ResponseEntity<SuccessResponseDto<List<FeedbackQuestionResponseDto>>> readFeedbackQuestion(
        @RequestParam("projectId") Long projectId
    ){
        log.info("/feedback/controller : 피드백 질문 리스트");
        return ResponseEntity.ok().body(feedbackService.readFeedbackQuestion(projectId));
    }

    @PostMapping(CREATE_FEEDBACK_ANSWER)
    @Operation(summary = "피드백 답변 생성")
    public ResponseEntity<SuccessResponseDto<FeedbackAnswerCreateResponseDto>> createFeedbackAnswer(
        @AuthenticationPrincipal String memberId,
        @RequestParam("projectId") Long projectId,
        @RequestBody List<FeedbackAnswerCreateRequestDto> requestBody
    ){
        log.info("/feedback/controller : 피드백 답변 생성");
        SuccessResponseDto<FeedbackAnswerCreateResponseDto> response = feedbackService.createFeedbackAnswer(memberId, projectId, requestBody);
        alarmService.createAlarm(memberId,"피드백" ,PostType.PROJECT, projectId);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping(ANALYSIS_FEEDBACK)
    @Operation(summary = "피드백 분석")
    public ResponseEntity<SuccessResponseDto<List<FeedbackAnalysisResponseDto>>> analysisFeedback(
        @AuthenticationPrincipal String memberId,
        @RequestParam("projectId") Long projectId
    ){
        log.info("/feedback/controller : 피드백 분석");
        return ResponseEntity.ok().body(feedbackService.analysisFeedback(memberId, projectId));
    }
}
