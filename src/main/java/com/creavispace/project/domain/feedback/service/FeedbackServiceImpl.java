package com.creavispace.project.domain.feedback.service;

import com.creavispace.project.common.post.entity.Post;
import com.creavispace.project.common.dto.response.SuccessResponseDto;
import com.creavispace.project.common.exception.CreaviCodeException;
import com.creavispace.project.common.exception.GlobalErrorCode;
import com.creavispace.project.domain.feedback.dto.request.FeedbackAnswerCreateRequestDto;
import com.creavispace.project.domain.feedback.dto.request.FeedbackQuestionCreateRequestDto;
import com.creavispace.project.domain.feedback.dto.response.ChoiceItemResponseDto;
import com.creavispace.project.domain.feedback.dto.response.FeedbackAnalysisResponseDto;
import com.creavispace.project.domain.feedback.dto.response.FeedbackQuestionResponseDto;
import com.creavispace.project.domain.feedback.dto.response.OptionAnalysisDto;
import com.creavispace.project.domain.feedback.entity.FeedbackAnswer;
import com.creavispace.project.domain.feedback.entity.FeedbackQuestion;
import com.creavispace.project.domain.feedback.entity.QuestionOption;
import com.creavispace.project.domain.feedback.repository.FeedbackAnswerRepository;
import com.creavispace.project.domain.feedback.repository.FeedbackQuestionRepository;
import com.creavispace.project.domain.feedback.repository.QuestionOptionRepository;
import com.creavispace.project.domain.member.entity.Role;
import com.creavispace.project.domain.member.entity.Member;
import com.creavispace.project.domain.member.repository.MemberRepository;
import com.creavispace.project.domain.project.entity.Project;
import com.creavispace.project.domain.project.repository.ProjectRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedbackServiceImpl implements FeedbackService {

    private final MemberRepository memberRepository;
    private final ProjectRepository projectRepository;
    private final FeedbackQuestionRepository feedbackQuestionRepository;
    private final FeedbackAnswerRepository feedbackAnswerRepository;
    private final QuestionOptionRepository questionOptionRepository;

    @Override
    @Transactional
    public SuccessResponseDto<Long> createFeedbackQuestion(String memberId, Long projectId,
                                                           FeedbackQuestionCreateRequestDto dto) {
        // 프로젝트 엔티티 조회
        Project project = projectRepository.findById(projectId).orElseThrow(() -> new NoSuchElementException("프로젝트 id(" + projectId + ")가 존재하지 않습니다."));

        // 피드백 질문 생성 권한 조회
        if(!project.getMember().getId().equals(memberId)) throw new CreaviCodeException(GlobalErrorCode.NOT_PERMISSMISSION);

        // 피드백 질문 생성
        FeedbackQuestion feedbackQuestion = FeedbackQuestion.builder().questionText(dto.getQuestion()).questionType(dto.getQuestionType()).build();
        for(String option : dto.getChoiceItems()){
            // 피드백 질문 옵션 생성 및 저장
            feedbackQuestion.addOption(QuestionOption.builder().optionText(option).build());
        }
        // 피드백 질문 저장
        project.addFeedbackQuestion(feedbackQuestion);

        // 성공 응답 반환
        return new SuccessResponseDto<>(true, "해당 피드백 질문 저장이 완료되었습니다.", feedbackQuestion.getId());
    }

    @Override
    @Transactional
    public SuccessResponseDto<Long> deleteFeedbackQuestion(String memberId, Long projectId,
            Long feedbackQuestionId) {
        // 프로젝트 엔티티 조회
        Project project = projectRepository.findById(projectId).orElseThrow(() -> new NoSuchElementException("프로젝트 id(" + projectId + ")가 존재하지 않습니다."));

        // 삭제 권한 조회
        if(!project.getMember().getId().equals(memberId)) throw new CreaviCodeException(GlobalErrorCode.NOT_PERMISSMISSION);

        // 피드백 질문 삭제
        feedbackQuestionRepository.deleteById(feedbackQuestionId);

        // 성공 응답 반환
        return new SuccessResponseDto<>(true, "해당 피드백 질문 삭제가 완료되었습니다.", feedbackQuestionId);
    }

    @Override
    public SuccessResponseDto<List<FeedbackQuestionResponseDto>> readFeedbackQuestion(Long projectId) {
        List<FeedbackQuestionResponseDto> data = null;

        // 프로젝트 엔티티 조회
        Project project = projectRepository.findById(projectId).orElseThrow(() -> new NoSuchElementException("프로젝트 id(" + projectId + ")가 존재하지 않습니다."));

        // 공개된 프로젝트인지
        if(!project.getStatus().equals(Post.Status.PUBLIC)) throw new CreaviCodeException(GlobalErrorCode.NOT_PUBLIC_CONTENT);

        // 피드백 질문 조회
        List<FeedbackQuestion> feedbackQuestions = project.getFeedbackQuestions();

        // 조회 결과 toDto
        data = feedbackQuestions.stream()
                .map(feedbackQuestion -> FeedbackQuestionResponseDto.builder()
                        .questionId(feedbackQuestion.getId())
                        .question(feedbackQuestion.getQuestionText())
                        .questionType(feedbackQuestion.getQuestionType().name())
                        .choiceItems(feedbackQuestion.getOptions().stream()
                                .map(option -> ChoiceItemResponseDto.builder()
                                        .id(option.getId())
                                        .item(option.getOptionText())
                                        .build())
                                .toList())
                        .build())
                .toList();

        // 성공 응답 반환
        return new SuccessResponseDto<>(true, "피드백 질문 리스트 조회가 완료되었습니다.", data);
    }

    @Override
    @Transactional
    public SuccessResponseDto<Long> createFeedbackAnswer(
            String memberId, Long projectId, List<FeedbackAnswerCreateRequestDto> dtos) {
        // 맴버 엔티티 조회
        Member member = memberRepository.findById(memberId).orElseThrow(()-> new NoSuchElementException("로그인 회원 아이디가 존재하지 않습니다."));

        // 프로젝트 엔티티 조회
        Project project = projectRepository.findById(projectId).orElseThrow(() -> new NoSuchElementException("프로젝트 id(" + projectId + ")가 존재하지 않습니다."));

        // 공개된 프로젝트인지
        if(!project.getStatus().equals(Post.Status.PUBLIC)) throw new CreaviCodeException(GlobalErrorCode.NOT_PUBLIC_CONTENT);

        // 피드백 답변 생성, 저장
        for (FeedbackAnswerCreateRequestDto dto : dtos) {
            Long questionId = dto.getQuestionId();
            // 피드백 질문 조회
            FeedbackQuestion feedbackQuestion = feedbackQuestionRepository.findById(questionId).orElseThrow(() -> new NoSuchElementException("질문 id(" + questionId + ")가 존재하지 않습니다."));

            // 피드백 답변 생성
            List<FeedbackAnswer> feedbackAnswers = dto.getSelectedItems().stream()
                    .map(choiceItemRequestDto -> {
                        Long selectedOptionId = choiceItemRequestDto.getId();
                        // 피드백 질문 옵션 조회
                        QuestionOption questionOption = selectedOptionId == null ? null : questionOptionRepository.findById(selectedOptionId).orElseThrow(() -> new NoSuchElementException("질문 옵션 id(" + selectedOptionId + ")가 존재하지 않습니다."));
                        return FeedbackAnswer.builder()
                                .answerText(dto.getAnswer())
                                .member(member)
                                .selectedOption(questionOption)
                                .build();
                    })
                    .toList();
            // 피드백 답변 저장
            for (FeedbackAnswer feedbackAnswer : feedbackAnswers) {
                feedbackQuestion.addAnswer(feedbackAnswer);
            }
        }

        // 성공 응답 반환
        return new SuccessResponseDto<>(true, "해당 프로젝트의 피드백 답변을 완료했습니다.", projectId);
    }

    @Override
    public SuccessResponseDto<List<FeedbackAnalysisResponseDto>> analysisFeedback(String memberId, Long projectId) {
        List<FeedbackAnalysisResponseDto> data = null;

        // 맴버 엔티티 조회
        Member member = memberRepository.findById(memberId).orElseThrow(()-> new NoSuchElementException("로그인 회원 아이디가 존재하지 않습니다."));

        // 프로젝트 엔티티 조회
        Project project = projectRepository.findById(projectId).orElseThrow(() -> new NoSuchElementException("프로젝트 id(" + projectId + ")가 존재하지 않습니다."));

        // 권한 조회
        if(!project.getMember().getId().equals(memberId) && !member.getRole().equals(Role.ADMIN)) throw new CreaviCodeException(GlobalErrorCode.NOT_PERMISSMISSION);

        // 피드백 분석 결과
        data = project.getFeedbackQuestions().stream().map(feedbackQuestion -> FeedbackAnalysisResponseDto.builder()
                        .questionId(feedbackQuestion.getId())
                        .questionText(feedbackQuestion.getQuestionText())
                        .questionType(feedbackQuestion.getQuestionType().name())
                        .selectedOptions(feedbackQuestion.getOptions().stream()
                                .map(questionOption -> {
                                    // 해당 옵션의 선택된 수 조회
                                    int count = feedbackAnswerRepository.countBySelectedOptionId(questionOption.getId());
                                    return OptionAnalysisDto.builder()
                                            .optionId(questionOption.getId())
                                            .optionText(questionOption.getOptionText())
                                            .selectedCount(count)
                                            .build();
                                })
                                .toList())
                        .answers(feedbackQuestion.getAnswers().stream().map(FeedbackAnswer::getAnswerText).toList())
                        .build())
                .toList();

        // 성공 응답 반환
        return new SuccessResponseDto<>(true, "피드백 분석 조회가 완료되었습니다.", data);

    }
    
}
