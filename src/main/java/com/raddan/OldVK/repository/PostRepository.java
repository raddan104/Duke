package com.raddan.OldVK.repository;

import com.raddan.OldVK.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    Optional<List<Post>> findAllByAuthorId(Long authorId);
    Page<Post> findAllByAuthorIdOrderByCreatedAtDesc(Long authorId, Pageable pageable);
    Page<Post> findAllByOrderByCreatedAtDesc(Pageable pageable);
    Long countByAuthorId(Long authorId);
}