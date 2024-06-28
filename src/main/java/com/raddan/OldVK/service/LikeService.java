package com.raddan.OldVK.service;

import com.raddan.OldVK.entity.Comment;
import com.raddan.OldVK.entity.Like;
import com.raddan.OldVK.entity.Post;
import com.raddan.OldVK.entity.User;
import com.raddan.OldVK.repository.CommentRepository;
import com.raddan.OldVK.repository.LikeRepository;
import com.raddan.OldVK.repository.PostRepository;
import com.raddan.OldVK.repository.UserRepository;
import com.raddan.OldVK.utils.AuthUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.*;

@Service
public class LikeService {

    private final Logger logger = LoggerFactory.getLogger(LikeService.class);

    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;

    public LikeService(LikeRepository likeRepository,
                       PostRepository postRepository,
                       UserRepository userRepository,
                       CommentRepository commentRepository) {
        this.likeRepository = likeRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
    }

    public ResponseEntity<?> createLikeForPost(Authentication authentication, Long postId) {
        try {
            UserDetails userDetails = AuthUtils.getUserDetails(authentication);
            User authorizedUser = userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException(userDetails.getUsername() + " not found"));

            Post post = postRepository.findById(postId)
                    .orElseThrow(() -> new RuntimeException("Post not found"));

            Like like = new Like();
            like.setPost(post);
            like.setUser(authorizedUser);
            like.setTimestamp(LocalDate.now());

            likeRepository.save(like);
            return ResponseEntity.ok("Like created successfully!");
        } catch (RuntimeException e) {
            logger.error("Can't create like for post: {}", e.getMessage());
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body("Failed to create like");
        }
    }

    public ResponseEntity<?> createLikeForComment(Authentication authentication,
                                                  Long postId,
                                                  Long commentId) {
        try {
            UserDetails userDetails = AuthUtils.getUserDetails(authentication);
            User authorizedUser = userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException(userDetails.getUsername() + " not found"));

            Comment comment = commentRepository.findById(commentId)
                    .orElseThrow(() -> new RuntimeException("Comment not found"));

            Like like = new Like();
            like.setComment(comment);
            like.setPost(comment.getPost());
            like.setUser(authorizedUser);
            like.setTimestamp(LocalDate.now());

            likeRepository.save(like);
            return ResponseEntity.ok("Like created successfully!");
        } catch (RuntimeException e) {
            logger.error("Can't create like for comment: {}", e.getMessage());
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body("Failed to create like");
        }
    }

    public ResponseEntity<?> getAllLikesByPostId(Long postId) {
        try {
            Post post = postRepository.findById(postId)
                    .orElseThrow(() -> new RuntimeException("Post not found"));

            Map<Long, String> likes = post.getLikes().stream()
                    .collect(Collectors.toConcurrentMap(
                            Like::getLikeId,
                            l -> l.getUser().getUsername()
                    ));

            return ResponseEntity.ok(likes);
        } catch (RuntimeException e) {
            logger.error("Can't get likes for post: {}", e.getMessage());
            throw new RuntimeException("Failed to get likes for post");
        }
    }

    public ResponseEntity<?> getAllLikesByCommentId(Long commentId) {
        try {
            Comment comment = commentRepository.findById(commentId)
                    .orElseThrow(() -> new RuntimeException("Comment not found"));

            Map<Long, String> likes = comment.getLikes().stream()
                    .collect(Collectors.toConcurrentMap(
                            Like::getLikeId,
                            l -> l.getUser().getUsername()
                    ));

            return ResponseEntity.ok(likes);
        } catch (RuntimeException e) {
            logger.error("Can't get likes for comment: {}", e.getMessage());
            throw new RuntimeException("Failed to get likes for comment");
        }
    }

    public ResponseEntity<?> deleteLike(Authentication authentication,
                                        Long likeId) {
        try {
            Like like = likeRepository.findById(likeId)
                    .orElseThrow(() -> new RuntimeException("Like not found with ID: " + likeId));

            UserDetails userDetails = AuthUtils.getUserDetails(authentication);
            User authorizedUser = userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException(userDetails.getUsername() + " not found"));

            if (!like.getUser().getUserID().equals(authorizedUser.getUserID())) {
                return ResponseEntity.status(FORBIDDEN).body("You can't delete this like!");
            }

            likeRepository.delete(like);

            return ResponseEntity.ok("Like with ID: " + likeId + " deleted successfully!");
        } catch (RuntimeException e) {
            logger.error("Can't delete like: {}", e.getMessage());
            return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                    .body("Failed to delete like with ID: " + likeId);
        }
    }
}
