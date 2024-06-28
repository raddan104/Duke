package com.raddan.OldVK.controller;

import com.raddan.OldVK.dto.CommentDTO;
import com.raddan.OldVK.service.CommentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/feed/{feedPostId}/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping
    public ResponseEntity<?> createComment(Authentication authentication,
                                           @PathVariable Long feedPostId,
                                           @RequestBody CommentDTO commentDTO) {
        return commentService.createComment(authentication, feedPostId, commentDTO.getContent());
    }

    @GetMapping
    public ResponseEntity<?> getAllCommentsByPost(@PathVariable Long feedPostId) {
        return commentService.getAllCommentsByPost(feedPostId);
    }

    @GetMapping("/{commentId}")
    public ResponseEntity<?> getCommentById(@PathVariable Long commentId) {
        return commentService.getCommentById(commentId);
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<?> updateComment(Authentication authentication,
                                           @PathVariable Long commentId,
                                           @RequestBody CommentDTO commentDTO) {
        return commentService.updateComment(authentication, commentId, commentDTO.getContent());
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<?> deleteComment(Authentication authentication,
                                           @PathVariable Long commentId) {
        return commentService.deleteComment(authentication, commentId);
    }
}
