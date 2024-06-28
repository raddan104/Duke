package com.raddan.OldVK.controller;

import com.raddan.OldVK.dto.PostDTO;
import com.raddan.OldVK.service.PostService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.Map;

@RestController
@RequestMapping("/feed")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping(path = "/{postID}")
    public ResponseEntity<?> getFeed(@PathVariable Long postID) {
        return postService.getPost(postID);
    }

    @PostMapping
    public ResponseEntity<?> createPost(Authentication authentication, @Valid @RequestBody PostDTO postDTO) throws SQLException {
        return postService.createPost(authentication, postDTO);
    }

    @PutMapping(path = "/{postID}")
    public ResponseEntity<?> updatePost(Authentication authentication,
                                        @RequestBody Map<String, Object> updatedData,
                                        @PathVariable Long postID) throws SQLException {
        return postService.updatePost(authentication, updatedData, postID);
    }

    @DeleteMapping(path = "/{postID}")
    public ResponseEntity<?> deletePost(Authentication authentication,
                                        @PathVariable Long postID) {
        return postService.deletePost(authentication, postID);
    }
}
