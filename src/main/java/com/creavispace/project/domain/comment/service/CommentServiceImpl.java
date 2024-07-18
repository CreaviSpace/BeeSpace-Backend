package com.creavispace.project.domain.comment.service;

import com.creavispace.project.common.dto.response.SuccessResponseDto;
import com.creavispace.project.common.dto.type.PostType;
import com.creavispace.project.common.exception.CreaviCodeException;
import com.creavispace.project.common.exception.GlobalErrorCode;
import com.creavispace.project.common.post.entity.Post;
import com.creavispace.project.common.post.repository.PostRepository;
import com.creavispace.project.domain.comment.dto.request.CommentRequestDto;
import com.creavispace.project.domain.comment.dto.response.CommentResponseDto;
import com.creavispace.project.domain.mypage.dto.response.MyPageCommentResponseDto;
import com.creavispace.project.domain.comment.entity.Comment;
import com.creavispace.project.domain.comment.repository.CommentRepository;
import com.creavispace.project.domain.member.entity.Member;
import com.creavispace.project.domain.member.entity.Role;
import com.creavispace.project.domain.member.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    @Override
    public SuccessResponseDto<List<CommentResponseDto>> readCommentList(Long postId, PostType postType) {
        // post 엔티티 조회
        Post post = postRepository.findById(postId).orElseThrow(() -> new NoSuchElementException("게시글 id("+postId+")가 존재하지 않습니다."));

        // 삭제된 게시글
        if(post.getStatus().equals(Post.Status.DELETE)) throw new CreaviCodeException("삭제된 게시글 입니다.",404);

        // 댓글 조회
        List<Comment> comments = commentRepository.findFetchJoinMemberByPostId(postId);

        // 결과값이 없으면 204 반환
        if(comments.isEmpty()) throw new CreaviCodeException("등록된 댓글이 없습니다.",200);

        // 댓글 리스트 결과 toDto
        List<CommentResponseDto> data = comments.stream()
                .map(comment -> CommentResponseDto.builder()
                        .id(comment.getId())
                        .memberId(comment.getMember().getId())
                        .memberNickName(comment.getMember().getMemberNickname())
                        .memberProfileUrl(comment.getMember().getProfileUrl())
                        .content(comment.getContent())
                        .modifiedDate(comment.getModifiedDate())
                        .build())
                .toList();
        
        // 성공 응답 반환
        return new SuccessResponseDto<>(true, "댓글 리스트 조회가 완료되었습니다.", data);
    }

    @Override
    @Transactional
    public SuccessResponseDto<CommentResponseDto> createComment(String memberId, Long postId, PostType postType, CommentRequestDto dto) {
        // 맴버 엔티티 조회
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new NoSuchElementException("로그인 회원 아이디가 존재하지 않습니다."));

        // Post 엔티티 조회
        Post post = postRepository.findById(postId).orElseThrow(() -> new NoSuchElementException("게시글 id(" + postId + ")가 존재하지 않습니다."));

        // 비공개 게시글
        if(!post.getStatus().equals(Post.Status.PUBLIC)) throw new CreaviCodeException("공개 게시글이 아닙니다.", 404);

        // 댓글 생성
        Comment comment = Comment.builder()
                .post(post)
                .member(member)
                .content(dto.getContent())
                .build();

        // 댓글 저장
        commentRepository.save(comment);

        // 댓글 결과 toDto
        CommentResponseDto data = CommentResponseDto.builder()
                .id(comment.getId())
                .memberId(member.getId())
                .memberNickName(member.getMemberNickname())
                .memberProfileUrl(member.getProfileUrl())
                .content(comment.getContent())
                .build();

        // 성공 응답 반환
        return new SuccessResponseDto<>(true, "댓글 생성이 완료되었습니다.", data);
    }

    @Override
    @Transactional
    public SuccessResponseDto<CommentResponseDto> modifyComment(String memberId, Long commentId, PostType postType, CommentRequestDto dto) {
        // 맴버 엔티티 조회
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new NoSuchElementException("로그인 회원 아이디가 존재하지 않습니다."));

        // 댓글 엔티티 조회
        Comment comment = commentRepository.findFetchJoinMemberById(commentId).orElseThrow(() -> new NoSuchElementException("댓글 id(" + commentId + ")가 존재하지 않습니다."));

        // 권한 조회
        if(!memberId.equals(comment.getMember().getId())) throw new CreaviCodeException(GlobalErrorCode.NOT_PERMISSMISSION);

        // 댓글 수정
        comment.changeContent(dto.getContent());
        
        // 댓글 toDto
        CommentResponseDto data = CommentResponseDto.builder()
                .id(comment.getId())
                .memberId(member.getId())
                .memberNickName(member.getMemberNickname())
                .memberProfileUrl(member.getProfileUrl())
                .content(comment.getContent())
                .build();
        
        // 성공 응답 반환
        return new SuccessResponseDto<>(true, "댓글 수정이 완료되었습니다.", data);
    }

    @Override
    @Transactional
    public SuccessResponseDto<Long> deleteComment(String memberId, Long commentId, PostType postType) {
        // 맴버 엔티티 조회
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new NoSuchElementException("로그인 회원 아이디가 존재하지 않습니다."));

        // 댓글 엔티티 조회
        Comment comment = commentRepository.findFetchJoinMemberById(commentId).orElseThrow(() -> new NoSuchElementException("댓글 id(" + commentId + ")가 존재하지 않습니다."));

        // 권한 조회
        if(!memberId.equals(comment.getMember().getId()) && !member.getRole().equals(Role.ADMIN)) throw new CreaviCodeException(GlobalErrorCode.NOT_PERMISSMISSION);

        // 댓글 삭제
        commentRepository.deleteById(commentId);
        
        // 성공 응답 반환
        return new SuccessResponseDto<>(true, "댓글 삭제가 완료되었습니다.", commentId);
    }

    @Override
    public SuccessResponseDto<List<MyPageCommentResponseDto>> mypageCommentPost(String memberId, PostType postType, Pageable pageable) {

        // 댓글 엔티티 조회
        Page<Comment> comments = commentRepository.findFetchJoinPostByMemberIdAndPostType(memberId, postType, pageable);

        // 결과값이 없으면 204 반환
        if(!comments.hasContent()) throw new CreaviCodeException("",204);

        // 댓글 리스트 결과 toDto
        List<MyPageCommentResponseDto> data = comments.getContent().stream()
                .map(comment -> MyPageCommentResponseDto.builder()
                        .comment(comment.getContent())
                        .postId(comment.getPost().getId())
                        .postTitle(comment.getPost().getTitle())
                        .postModifiedDate(comment.getPost().getModifiedDate())
                        .build())
                .toList();

        // 성공 응답 반환
        return new SuccessResponseDto<>(true, "댓글 리스트 조회가 완료되었습니다.", data);
    }
}
