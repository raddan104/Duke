package com.raddan.OldVK.service;

import com.raddan.OldVK.dto.CommentDTO;
import com.raddan.OldVK.entity.Comment;
import com.raddan.OldVK.entity.Post;
import com.raddan.OldVK.entity.User;
import com.raddan.OldVK.repository.CommentRepository;
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

import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Service
public class CommentService {

    private final Logger logger = LoggerFactory.getLogger(CommentService.class);

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public CommentService(CommentRepository commentRepository,
                          PostRepository postRepository,
                          UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    public ResponseEntity<?> createComment(Authentication authentication,
                                           Long postId,
                                           String content) {
        try {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            User authorizedUser = userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException(userDetails.getUsername() + " not found"));

            Post post = postRepository.findByPost_Id(postId)
                    .orElseThrow(() -> new RuntimeException("Post not found"));

            Comment comment = new Comment();
            comment.setUser(authorizedUser);
            comment.setPost(post);
            comment.setContent(content.trim());
            comment.setCreatedAt(LocalDate.now());
            commentRepository.save(comment);

            return ResponseEntity.ok("Comment created successfully with ID: " + comment.getID());
        } catch (RuntimeException e) {
            logger.error("Can't create comment: {}", e.getMessage());
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body("Failed to create comment");
        }
    }

    public ResponseEntity<?> getAllCommentsByPost(Long postId) {
        try {
            Post post = postRepository.findByPost_Id(postId)
                    .orElseThrow(() -> new RuntimeException("Post not found"));

            Map<String, String> comments = post.getComments().stream()
                    .collect(Collectors.toConcurrentMap(
                            c -> c.getUser().getUsername(),
                            Comment::getContent
                    ));

            return ResponseEntity.ok(comments);
        } catch (RuntimeException e) {
            logger.error("Can't get comments for post: {}", e.getMessage());
            throw new RuntimeException("Failed to get comments for post");
        }
    }

    public ResponseEntity<?> getCommentById(Long commentId) {
        try {
            Comment comment = commentRepository.findByCommentId(commentId)
                    .orElseThrow(() -> new RuntimeException("Comment not found!"));

            CommentDTO commentDTO = new CommentDTO();
            commentDTO.setCommentID(comment.getID());
            commentDTO.setContent(comment.getContent());
            commentDTO.setTimestamp(comment.getCreatedAt());
            commentDTO.setUsername(comment.getUser().getUsername());
            commentDTO.setPostID(comment.getPost().getID());

            return ResponseEntity.ok(commentDTO);
        } catch (RuntimeException e) {
            logger.error("Can't get comment: {}", e.getMessage());
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body("Failed to get comment");
        }
    }

    public ResponseEntity<?> updateComment(Authentication authentication,
                                           Long commentId,
                                           String newContent) {
        try {
            Comment comment = commentRepository.findByCommentId(commentId)
                    .orElseThrow(() -> new RuntimeException("Comment not found!"));

            UserDetails userDetails = AuthUtils.getUserDetails(authentication);
            User authorizedUser = userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException(userDetails.getUsername() + " not found"));

            if (!comment.getUser().getID().equals(authorizedUser.getID())) {
                return ResponseEntity.status(FORBIDDEN).body("You can't edit this comment!");
            }

            comment.setContent(newContent);
            commentRepository.save(comment);

            return ResponseEntity.ok(String.format("Comment with ID '%s', successfully updated!", commentId));
        } catch (RuntimeException e) {
            logger.error("Can't update comment: {}", e.getMessage());
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body("Failed to update comment");
        }
    }

    public ResponseEntity<?> deleteComment(Authentication authentication, Long commentId) {
        Comment comment = commentRepository.findByCommentId(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found with ID: " + commentId));

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User authorizedUser = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException(userDetails.getUsername() + " not found"));

        if (!comment.getUser().getID().equals(authorizedUser.getID())) {
            return ResponseEntity.status(FORBIDDEN).body("You can't delete this comment!");
        }

        try {
            commentRepository.delete(comment);
            return ResponseEntity.ok(String.format("Comment '%s' deleted.", commentId));
        } catch (RuntimeException e) {
            logger.error("Can't delete comment with ID: {}. {}", commentId, e.getMessage());
            return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                    .body(String.format("Can't delete comment with ID: '%s'. '%s'", commentId, e.getMessage()));
        }
    }
}
