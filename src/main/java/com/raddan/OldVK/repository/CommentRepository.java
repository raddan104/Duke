package com.raddan.OldVK.repository;

import com.raddan.OldVK.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    Optional<List<Comment>> findAllByPostId(Long postId);
    Page<Comment> findAllByPostIdOrderByCreatedAtDesc(Long postId, Pageable pageable);
    Optional<List<Comment>> findAllByAuthorId(Long authorId);
    long countByPostId(Long postId);
    long countByAuthorId(Long authorId);
}
