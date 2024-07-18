package com.creavispace.project.domain.feedback.service;

import com.creavispace.project.common.dto.response.SuccessResponseDto;
import com.creavispace.project.domain.feedback.dto.request.FeedbackAnswerCreateRequestDto;
import com.creavispace.project.domain.feedback.dto.request.FeedbackQuestionCreateRequestDto;
import com.creavispace.project.domain.feedback.dto.response.FeedbackAnalysisResponseDto;
import com.creavispace.project.domain.feedback.dto.response.FeedbackQuestionResponseDto;

import java.util.List;

public interface FeedbackService {
    public SuccessResponseDto<Long> createFeedbackQuestion(String memberId, Long projectId, FeedbackQuestionCreateRequestDto dto);
    public SuccessResponseDto<Long> deleteFeedbackQuestion(String memberId, Long projectId, Long questionId);
    public SuccessResponseDto<List<FeedbackQuestionResponseDto>> readFeedbackQuestion(Long projectId);
    public SuccessResponseDto<Long> createFeedbackAnswer(String memberId, Long projectId, List<FeedbackAnswerCreateRequestDto> dtos);
    public SuccessResponseDto<List<FeedbackAnalysisResponseDto>> analysisFeedback(String memberId, Long projectId);
}
