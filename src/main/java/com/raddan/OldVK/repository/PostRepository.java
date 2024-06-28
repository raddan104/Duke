package com.raddan.OldVK.repository;

import com.raddan.OldVK.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    @Query("SELECT p FROM Post p JOIN p.user u WHERE u.userId = :userId")
    Optional<Post> findByUserId(Long userId);
}
