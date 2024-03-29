package com.creavispace.project.domain.feedback.service;

import java.util.List;

import com.creavispace.project.domain.common.dto.SuccessResponseDto;
import com.creavispace.project.domain.feedback.dto.request.FeedbackAnswerCreateRequestDto;
import com.creavispace.project.domain.feedback.dto.request.FeedbackQuestionCreateRequestDto;
import com.creavispace.project.domain.feedback.dto.request.FeedbackQuestionModifyRequestDto;
import com.creavispace.project.domain.feedback.dto.response.FeedbackAnalysisResponseDto;
import com.creavispace.project.domain.feedback.dto.response.FeedbackAnswerCreateResponseDto;
import com.creavispace.project.domain.feedback.dto.response.FeedbackQuestionResponseDto;

public interface FeedbackService {
    public SuccessResponseDto<List<FeedbackQuestionResponseDto>> createFeedbackQuestion(Long memberId, Long projectId, List<FeedbackQuestionCreateRequestDto> dtos);
    public SuccessResponseDto<List<FeedbackQuestionResponseDto>> modifyFeedbackQuestion(Long memberId, Long projectId, List<FeedbackQuestionModifyRequestDto> dtos);
    public SuccessResponseDto<List<FeedbackQuestionResponseDto>> readFeedbackQuestion(Long projectId);
    public SuccessResponseDto<FeedbackAnswerCreateResponseDto> createFeedbackAnswer(Long memberId, Long projectId, List<FeedbackAnswerCreateRequestDto> dtos);
    public SuccessResponseDto<List<FeedbackAnalysisResponseDto>> analysisFeedback(Long memberId, Long projectId);
}
