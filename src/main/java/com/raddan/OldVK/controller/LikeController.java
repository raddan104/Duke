package com.raddan.OldVK.controller;

import com.raddan.OldVK.service.LikeService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/feed/{postId}/")
public class LikeController {

    private final LikeService likeService;

    public LikeController(LikeService likeService) {
        this.likeService = likeService;
    }

    @PostMapping(path = "/like")
    public ResponseEntity<?> createLikeForPost(Authentication authentication,
                                               @PathVariable Long postId) {
        return likeService.createLikeForPost(authentication, postId);
    }

    @PostMapping(path = "/comments/{commentId}/like")
    public ResponseEntity<?> createLikeForComment(Authentication authentication,
                                                  @PathVariable Long postId,
                                                  @PathVariable Long commentId) {
        return likeService.createLikeForComment(authentication, postId, commentId);
    }

    @GetMapping(path = "/likes")
    public ResponseEntity<?> getAllLikesForPost(@PathVariable Long postId) {
        return likeService.getAllLikesByPostId(postId);
    }

    @GetMapping(path = "/comments/{commentId}/likes")
    public ResponseEntity<?> getAllLikesForComment(@PathVariable Long postId,
                                                   @PathVariable Long commentId) {
        return likeService.getAllLikesByCommentId(commentId);
    }

    @DeleteMapping(path = "/likes/{likeId}")
    public ResponseEntity<?> deleteLike(Authentication authentication,
                                        @PathVariable Long postId,
                                        @PathVariable Long likeId) {
        return likeService.deleteLike(authentication, likeId);
    }
}
