package com.raddan.OldVK.controller;

import com.raddan.OldVK.dto.PostDTO;
import com.raddan.OldVK.service.PostService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;

@RestController
@RequestMapping("/feed")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping(path = "/{postID}")
    public ResponseEntity<?> getFeed(Authentication authentication,
                                     @PathVariable Long postID) {
        return postService.getPost(authentication, postID);
    }

    @PostMapping
    public ResponseEntity<?> createPost(Authentication authentication, @Valid @RequestBody PostDTO postDTO) throws SQLException {
        return postService.createPost(authentication, postDTO);
    }

}
