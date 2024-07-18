package com.creavispace.project.domain.comment.repository;

import com.creavispace.project.common.dto.type.PostType;
import com.creavispace.project.domain.comment.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @EntityGraph(attributePaths = {"member"})
    public List<Comment> findFetchJoinMemberByPostId(Long postId);

    @EntityGraph(attributePaths = {"member"})
    public Optional<Comment> findFetchJoinMemberById(Long commentId);

    @Query(value = "SELECT c FROM Comment c"
    + " JOIN FETCH c.post p"
    + " WHERE c.member = :memberId"
    + " AND p.postType = :postType")
    public Page<Comment> findFetchJoinPostByMemberIdAndPostType(@Param("memberId") String memberId, @Param("postType") PostType postType, Pageable pageable);
}
