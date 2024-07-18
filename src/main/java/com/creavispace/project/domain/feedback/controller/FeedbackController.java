package com.creavispace.project.domain.feedback.controller;

import com.creavispace.project.common.dto.response.SuccessResponseDto;
import com.creavispace.project.common.dto.type.PostType;
import com.creavispace.project.domain.alarm.service.AlarmService;
import com.creavispace.project.domain.feedback.dto.request.FeedbackAnswerCreateRequestDto;
import com.creavispace.project.domain.feedback.dto.request.FeedbackQuestionCreateRequestDto;
import com.creavispace.project.domain.feedback.dto.response.FeedbackAnalysisResponseDto;
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
    private final static String DELETE_FEEDBACK_QUESTION = "/question";
    private final static String READ_FEEDBACK_QUESTION = "/question";
    private final static String CREATE_FEEDBACK_ANSWER = "/answer";
    private final static String ANALYSIS_FEEDBACK = "/analysis";

    @PostMapping(CREATE_FEEDBACK_QUESTION)
    @Operation(summary = "피드백 질문 생성")
    public ResponseEntity<SuccessResponseDto<Long>> createFeedbackQuestion(
        @AuthenticationPrincipal String memberId,
        @RequestParam("projectId") Long projectId, 
        @RequestBody FeedbackQuestionCreateRequestDto requestBody
    ){
        log.info("/feedback/controller : 피드백 질문 생성");
        return ResponseEntity.ok().body(feedbackService.createFeedbackQuestion(memberId, projectId, requestBody));
    }

    @PutMapping(DELETE_FEEDBACK_QUESTION)
    @Operation(summary = "피드백 질문 삭제")
    public ResponseEntity<SuccessResponseDto<Long>> modifyFeedbackQuestion(
        @AuthenticationPrincipal String memberId,
        @RequestParam("projectId") Long projectId,
        @RequestParam("questionId") Long questionId
    ){
        log.info("/feedback/controller : 피드백 질문 수정");
        return ResponseEntity.ok().body(feedbackService.deleteFeedbackQuestion(memberId, projectId, questionId));
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
    public ResponseEntity<SuccessResponseDto<Long>> createFeedbackAnswer(
        @AuthenticationPrincipal String memberId,
        @RequestParam("projectId") Long projectId,
        @RequestBody List<FeedbackAnswerCreateRequestDto> requestBody
    ){
        log.info("/feedback/controller : 피드백 답변 생성");
        SuccessResponseDto<Long> response = feedbackService.createFeedbackAnswer(memberId, projectId, requestBody);
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
