package com.creavispace.project.domain.feedback.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.creavispace.project.domain.common.dto.response.SuccessResponseDto;
import com.creavispace.project.domain.common.dto.type.QuestionType;
import com.creavispace.project.domain.feedback.dto.request.FeedbackAnswerCreateRequestDto;
import com.creavispace.project.domain.feedback.dto.request.FeedbackQuestionCreateRequestDto;
import com.creavispace.project.domain.feedback.dto.request.FeedbackQuestionModifyRequestDto;
import com.creavispace.project.domain.feedback.dto.response.ChoiceItemResponseDto;
import com.creavispace.project.domain.feedback.dto.response.ChoiceItemsAnalysisResponseDto;
import com.creavispace.project.domain.feedback.dto.response.FeedbackAnalysisResponseDto;
import com.creavispace.project.domain.feedback.dto.response.FeedbackAnswerCreateResponseDto;
import com.creavispace.project.domain.feedback.dto.response.FeedbackQuestionResponseDto;
import com.creavispace.project.domain.feedback.dto.response.ObjectiveFeedbackAnalysisResponseDto;
import com.creavispace.project.domain.feedback.dto.response.SubjectiveFeedbackAnalysisResponseDto;
import com.creavispace.project.domain.feedback.entity.ChoiceItem;
import com.creavispace.project.domain.feedback.entity.FeedbackAnswer;
import com.creavispace.project.domain.feedback.entity.FeedbackQuestion;
import com.creavispace.project.domain.feedback.entity.SelectedItem;
import com.creavispace.project.domain.feedback.repository.ChoiceItemRepository;
import com.creavispace.project.domain.feedback.repository.FeedbackAnswerRepository;
import com.creavispace.project.domain.feedback.repository.FeedbackQuestionRepository;
import com.creavispace.project.domain.feedback.repository.SelectedItemRepository;
import com.creavispace.project.domain.member.entity.Member;
import com.creavispace.project.domain.member.repository.MemberRepository;
import com.creavispace.project.domain.project.entity.Project;
import com.creavispace.project.domain.project.repository.ProjectRepository;
import com.creavispace.project.global.exception.CreaviCodeException;
import com.creavispace.project.global.exception.GlobalErrorCode;
import com.creavispace.project.global.util.CustomValueOf;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedbackServiceImpl implements FeedbackService {

    private final MemberRepository memberRepository;
    private final ProjectRepository projectRepository;
    private final FeedbackQuestionRepository feedbackQuestionRepository;
    private final ChoiceItemRepository choiceItemRepository;
    private final FeedbackAnswerRepository feedbackAnswerRepository;
    private final SelectedItemRepository selectedItemRepository;

    @Override
    @Transactional
    public SuccessResponseDto<List<FeedbackQuestionResponseDto>> createFeedbackQuestion(Long memberId, Long projectId,
            List<FeedbackQuestionCreateRequestDto> dtos) {
        List<FeedbackQuestionResponseDto> data = null;
        // JWT에 저장된 회원이 존재하는지
        if(!memberRepository.existsById(memberId)) throw new CreaviCodeException(GlobalErrorCode.MEMBER_NOT_FOUND);

        // 프로젝트가 존재하는지
        Project project = projectRepository.findById(projectId).orElseThrow(()-> new CreaviCodeException(GlobalErrorCode.PROJECT_NOT_FOUND));
        
        // 피드백 질문 생성 및 저장
        List<FeedbackQuestion> feedbackQuestions = dtos.stream()
            .map(dto -> {
                // 질문 타입이 존재하는지
                QuestionType questionType = CustomValueOf.valueOf(QuestionType.class, dto.getQuestionType(), GlobalErrorCode.NOT_FOUND_QUESTION_TYPE);
                // 질문 생성
                FeedbackQuestion feedbackQuestion = FeedbackQuestion.builder()
                .project(project)
                .question(dto.getQuestion())
                .questionType(questionType)
                .build();
                // 질문 저장
                feedbackQuestionRepository.save(feedbackQuestion);

                // 객관식 or 체크박스 타입인지
                if(dto.getQuestionType().equals("OBJECTIVE") || dto.getQuestionType().equals("CHECKBOX")){
                    // 선택지 생성
                    List<ChoiceItem> choiceItems = dto.getChoiceItems().stream()
                        .map(choiceItem -> ChoiceItem.builder()
                            .feedbackQuestion(feedbackQuestion)
                            .item(choiceItem)
                            .build())
                        .collect(Collectors.toList());
                    // 선택지 저장
                    choiceItemRepository.saveAll(choiceItems);
                }

                return feedbackQuestion;
            })
            .collect(Collectors.toList());

        // 피드백 질문 DTO
        data = feedbackQuestions.stream()
            .map(feedbackQuestion -> {
                List<ChoiceItem> choiceItems = choiceItemRepository.findByFeedbackQuestionId(feedbackQuestion.getId());
                
                return FeedbackQuestionResponseDto.builder()
                    .questionId(feedbackQuestion.getId())
                    .question(feedbackQuestion.getQuestion())
                    .questionType(feedbackQuestion.getQuestionType().name())
                    .choiceItems(choiceItems.stream()
                        .map(choiceItem -> ChoiceItemResponseDto.builder()
                            .id(choiceItem.getId())
                            .item(choiceItem.getItem())
                            .build())
                        .collect(Collectors.toList()))
                    .build();
                })
            .collect(Collectors.toList());

        log.info("/feedback/service : createFeedbackQuestion success data = {}", data);
        // 성공 응답 반환
        return new SuccessResponseDto<>(true, "피드백 질문 저장이 완료되었습니다.", data);

    }

    @Override
    @Transactional
    public SuccessResponseDto<List<FeedbackQuestionResponseDto>> modifyFeedbackQuestion(Long memberId, Long projectId,
            List<FeedbackQuestionModifyRequestDto> dtos) {
        List<FeedbackQuestionResponseDto> data = null;
        // JWT에 저장된 회원이 존재하는지
        Member member = memberRepository.findById(memberId).orElseThrow(()-> new CreaviCodeException(GlobalErrorCode.MEMBER_NOT_FOUND));

        // 프로젝트가 존재하는지
        Project project = projectRepository.findById(projectId).orElseThrow(()-> new CreaviCodeException(GlobalErrorCode.PROJECT_NOT_FOUND));

        // 수정 권한이 있는지
        if(project.getMember().getId() != memberId && !member.getRole().equals("Administrator")){
            throw new CreaviCodeException(GlobalErrorCode.NOT_PERMISSMISSION);
        }

        // 피드백 질문을 모두 삭제했다면
        if(dtos == null || dtos.isEmpty()) {
            feedbackQuestionRepository.deleteByProjectId(projectId);
            return new SuccessResponseDto<>(true, "피드백 질문 추가/삭제 저장이 완료되었습니다.", new ArrayList<FeedbackQuestionResponseDto>());
        }

        // 수정할 피드백 질문 ID 그룹화
        List<Long> modifyFeedbackQuestionIds = dtos.stream()
            .filter(dto -> dto.getQuestionId() != null)
            .map(dto -> dto.getQuestionId())
            .collect(Collectors.toList());

        // 일부 삭제된 피드백 질문 삭제
        if(modifyFeedbackQuestionIds != null && !modifyFeedbackQuestionIds.isEmpty())
            feedbackQuestionRepository.deleteByQuestionIds(modifyFeedbackQuestionIds);

        // modify 피드백
        List<FeedbackQuestion> feedbackQuestions = dtos.stream()
            .map(dto-> {
                // 기존에 있던 피드백인 경우 그대로 리턴
                if(dto.getQuestionId() != null){
                    return feedbackQuestionRepository.findById(dto.getQuestionId()).orElseThrow(() -> new CreaviCodeException(GlobalErrorCode.FEEDBACK_QUESTION_NOT_FOUND));
                }
                // 신규 피드백은 저장 후 리턴
                else{
                    // 피드백 질문 타입이 존재하는지
                    QuestionType questionType = CustomValueOf.valueOf(QuestionType.class, dto.getQuestionType(), GlobalErrorCode.NOT_FOUND_QUESTION_TYPE);
                    // 신규 피드백 저장
                    FeedbackQuestion feedbackQuestion = FeedbackQuestion.builder()
                    .project(project)
                    .question(dto.getQuestion())
                    .questionType(questionType)
                    .build();
                    // 신규 피드백 저장
                    feedbackQuestionRepository.save(feedbackQuestion);
    
                    // 객관식 or 체크박스일 경우 선택지 저장
                    if(dto.getQuestionType().equals("OBJECTIVE") || dto.getQuestionType().equals("CHECKBOX")){
                        // 선택지 생성
                        List<ChoiceItem> choiceItems = dto.getChoiceItems().stream()
                            .map(choiceTiem -> ChoiceItem.builder()
                                .feedbackQuestion(feedbackQuestion)
                                .item(choiceTiem)
                                .build())
                            .collect(Collectors.toList());
                        // 선택지 저장
                        choiceItemRepository.saveAll(choiceItems);
                    }
                    return feedbackQuestion;
                }
            })
            .collect(Collectors.toList());

        // 피드백 질문 DTO
        data = feedbackQuestions.stream()
            .map(feedbackQuestion -> {
                List<ChoiceItem> choiceItems = choiceItemRepository.findByFeedbackQuestionId(feedbackQuestion.getId());

                return FeedbackQuestionResponseDto.builder()
                    .questionId(feedbackQuestion.getId())
                    .question(feedbackQuestion.getQuestion())
                    .questionType(feedbackQuestion.getQuestionType().name())
                    .choiceItems(choiceItems.stream()
                        .map(choiceItem -> ChoiceItemResponseDto.builder()
                            .id(choiceItem.getId())
                            .item(choiceItem.getItem())
                            .build())
                        .collect(Collectors.toList()))
                    .build();
                })
            .collect(Collectors.toList());

        log.info("/feedback/service : modifyFeedbackQuestion success data = {}", data);
        // 성공 응답 반환
        return new SuccessResponseDto<>(true, "피드백 질문 추가/삭제 저장이 완료되었습니다.", data);
    }

    @Override
    public SuccessResponseDto<List<FeedbackQuestionResponseDto>> readFeedbackQuestion(Long projectId) {
        List<FeedbackQuestionResponseDto> data = null;

        // 피드백 질문 리스트 가져오기
        List<FeedbackQuestion> feedbackQuestions = feedbackQuestionRepository.findByProjectId(projectId);

        // 피드백 질문 리스트 DTO
        data = feedbackQuestions.stream()
            .map(feedbackQuestion -> {
                List<ChoiceItem> choiceItems = choiceItemRepository.findByFeedbackQuestionId(feedbackQuestion.getId());
                return FeedbackQuestionResponseDto.builder()
                .questionId(feedbackQuestion.getId())
                .question(feedbackQuestion.getQuestion())
                .questionType(feedbackQuestion.getQuestionType().name())
                .choiceItems(choiceItems.stream()
                    .map(choiceItem -> ChoiceItemResponseDto.builder()
                        .id(choiceItem.getId())
                        .item(choiceItem.getItem())
                        .build())
                    .collect(Collectors.toList()))
                .build();
            })
            .collect(Collectors.toList());

        log.info("/feedback/service : readFeedbackQuestion success data = {}", data);
        // 성공 응답 반환
        return new SuccessResponseDto<>(true, "피드백 질문 리스트 조회가 완료되었습니다.", data);
    }

    @Override
    @Transactional
    public SuccessResponseDto<FeedbackAnswerCreateResponseDto> createFeedbackAnswer(
            Long memberId, Long projectId, List<FeedbackAnswerCreateRequestDto> dtos) {
        FeedbackAnswerCreateResponseDto data = null;

        // JWT에 저장된 회원이 존재하는지
        Member member = memberRepository.findById(memberId).orElseThrow(()-> new CreaviCodeException(GlobalErrorCode.MEMBER_NOT_FOUND));

        // 피드백 답변이 있는지
        if(dtos == null || dtos.isEmpty()) throw new CreaviCodeException(GlobalErrorCode.NOT_FEEDBACK_ANSWER_CONTENT);
        
        // 피드백 답변 저장
        List<FeedbackAnswer> feedbackAnswers = dtos.stream()
                .map(dto -> {
                    // 존재하는 질문인지
                    FeedbackQuestion feedbackQuestion = feedbackQuestionRepository.findById(dto.getQuestionId()).orElseThrow(() -> new CreaviCodeException(GlobalErrorCode.FEEDBACK_QUESTION_NOT_FOUND));
                    // 답변 생성
                    FeedbackAnswer feedbackAnswer = FeedbackAnswer.builder()
                        .feedbackQuestion(feedbackQuestion)
                        .member(member)
                        .answer(dto.getAnswer())
                        .build();
                    // 답변 저장
                    feedbackAnswerRepository.save(feedbackAnswer);

                    // 객관식이나 체크박스일 경우 선택지 답변 저장
                    if(feedbackQuestion.getQuestionType().name().equals("OBJECTIVE") || feedbackQuestion.getQuestionType().name().equals("CHECKBOX")){

                        List<SelectedItem> selectedItems = dto.getSelectedItems().stream()
                            .map(selectedItemDto -> {
                                ChoiceItem choiceItem = choiceItemRepository.findByIdAndFeedbackQuestionId(selectedItemDto.getId(), dto.getQuestionId()).orElseThrow(()-> new CreaviCodeException(GlobalErrorCode.CHOICE_ITEM_NOT_FOUND));

                                return SelectedItem.builder()
                                    .feedbackAnswer(feedbackAnswer)
                                    .choiceItem(choiceItem)
                                    .build();
                                })
                            .collect(Collectors.toList());
    
                        selectedItemRepository.saveAll(selectedItems);
                    }
                        
                    return feedbackAnswer;
                })
                .collect(Collectors.toList());

        data = FeedbackAnswerCreateResponseDto.builder().projectId(projectId).build();

        log.info("/feedback/service : createFeedbackAnswer success data = {}", data);
        // 성공 응답 반환
        return new SuccessResponseDto<>(true, "해당 프로젝트의 피드백 답변을 완료했습니다.", data);
    }

    @Override
    public SuccessResponseDto<List<FeedbackAnalysisResponseDto>> analysisFeedback(Long memberId, Long projectId) {
        List<FeedbackAnalysisResponseDto> data = null;
        // JWT에 저장된 회원이 존재하는지
        Member member = memberRepository.findById(memberId).orElseThrow(()-> new CreaviCodeException(GlobalErrorCode.MEMBER_NOT_FOUND));

        // 프로젝트가 존재하는지
        Project project = projectRepository.findById(projectId).orElseThrow(()-> new CreaviCodeException(GlobalErrorCode.PROJECT_NOT_FOUND));

        // 피드백 분석 조회 권한이 있는지
        if(project.getMember().getId() != memberId && !member.getRole().equals("Administrator")){
            throw new CreaviCodeException(GlobalErrorCode.NOT_PERMISSMISSION);
        }

        // 피드백 질문 가져오기
        List<FeedbackQuestion> feedbackQuestions = feedbackQuestionRepository.findByProjectId(projectId);

        // 피드백 질문이 없다면
        if(feedbackQuestions == null || feedbackQuestions.isEmpty()) throw new CreaviCodeException(GlobalErrorCode.NOT_QUESTION_ANALYSIS_CONTENT);

        // 피드백 분석 DTO
        data = feedbackQuestions.stream()
            .map(feedbackQuestion -> {
                switch (feedbackQuestion.getQuestionType().name()) {
                    case "OBJECTIVE":
                        return ObjectiveFeedbackAnalysisResponseDto.builder()
                        .question(feedbackQuestion.getQuestion())
                        .questionType(feedbackQuestion.getQuestionType().name())
                        .choiceItemsAnalysis(feedbackQuestion.getChoiceItems().stream()
                            .map(choiceItem -> {
                                return ChoiceItemsAnalysisResponseDto.builder()
                                .choiceItem(choiceItem.getItem())
                                .selectedCount(selectedItemRepository.countByChoiceItemId(choiceItem.getId()))
                                .build();
                            })
                            .collect(Collectors.toList()))
                        .build();    
                    case "CHECKBOX":
                        return ObjectiveFeedbackAnalysisResponseDto.builder()
                        .question(feedbackQuestion.getQuestion())
                        .questionType(feedbackQuestion.getQuestionType().name())
                        .choiceItemsAnalysis(feedbackQuestion.getChoiceItems().stream()
                            .map(choiceItem -> {
                                return ChoiceItemsAnalysisResponseDto.builder()
                                .choiceItem(choiceItem.getItem())
                                .selectedCount(selectedItemRepository.countByChoiceItemId(choiceItem.getId()))
                                .build();
                            })
                            .collect(Collectors.toList()))
                        .build();    
                    case "SUBJECTIVE":
                        List<FeedbackAnswer> feedbackAnswers = feedbackAnswerRepository.findByFeedbackQuestionId(feedbackQuestion.getId());
                        return SubjectiveFeedbackAnalysisResponseDto.builder()
                            .question(feedbackQuestion.getQuestion())
                            .questionType(feedbackQuestion.getQuestionType().name())
                            .answers(feedbackAnswers.stream()
                                .map(feedbackAnswer -> feedbackAnswer.getAnswer())
                                .collect(Collectors.toList()))
                            .build();
                    default:
                        throw new CreaviCodeException(GlobalErrorCode.NOT_FOUND_QUESTION_TYPE);
                }
            })
            .collect(Collectors.toList());

        log.info("/feedback/service : analysisFeedback success data = {}", data);
        // 성공 응답 반환
        return new SuccessResponseDto<>(true, "피드백 분석 조회가 완료되었습니다.", data);

    }
    
}
