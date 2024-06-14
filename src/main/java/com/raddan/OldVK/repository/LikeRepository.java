package com.raddan.OldVK.repository;

import com.raddan.OldVK.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {
    Like findByPostIdAndUserId(Long postId, Long userId);
    Optional<List<Like>> findAllByPostId(Long postId);
    Optional<List<Like>> findAllByUserId(Long userId);
    long countByPostId(Long postId);
    long countByUserId(Long userId);
    boolean existsByPostIdAndUserId(Long postId, Long userId);
    void deleteByPostIdAndUserId(Long postId, Long userId);
}
