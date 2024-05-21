package com.creavispace.project.domain.feedback.service;

import com.creavispace.project.common.dto.response.SuccessResponseDto;
import com.creavispace.project.domain.feedback.dto.request.FeedbackAnswerCreateRequestDto;
import com.creavispace.project.domain.feedback.dto.request.FeedbackQuestionCreateRequestDto;
import com.creavispace.project.domain.feedback.dto.request.FeedbackQuestionModifyRequestDto;
import com.creavispace.project.domain.feedback.dto.response.FeedbackAnalysisResponseDto;
import com.creavispace.project.domain.feedback.dto.response.FeedbackAnswerCreateResponseDto;
import com.creavispace.project.domain.feedback.dto.response.FeedbackQuestionResponseDto;

import java.util.List;

public interface FeedbackService {
    public SuccessResponseDto<List<FeedbackQuestionResponseDto>> createFeedbackQuestion(String memberId, Long projectId, List<FeedbackQuestionCreateRequestDto> dtos);
    public SuccessResponseDto<List<FeedbackQuestionResponseDto>> modifyFeedbackQuestion(String memberId, Long projectId, List<FeedbackQuestionModifyRequestDto> dtos);
    public SuccessResponseDto<List<FeedbackQuestionResponseDto>> readFeedbackQuestion(Long projectId);
    public SuccessResponseDto<FeedbackAnswerCreateResponseDto> createFeedbackAnswer(String memberId, Long projectId, List<FeedbackAnswerCreateRequestDto> dtos);
    public SuccessResponseDto<List<FeedbackAnalysisResponseDto>> analysisFeedback(String memberId, Long projectId);
}
