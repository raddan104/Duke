package com.raddan.OldVK.controller;

import com.raddan.OldVK.entity.Post;
import com.raddan.OldVK.entity.dto.PostRequest;
import com.raddan.OldVK.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/feed")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @GetMapping("/{postId}")
    public Post getPostInfoByID(@PathVariable Long postId) {
        return postService.getPostById(postId);
    }

    @GetMapping
    public Map<Long, String> getAllPosts() {
        return postService.getAllPosts();
    }

    @PostMapping
    public String createPost(@RequestBody PostRequest postRequest) {
       return postService.createPost(postRequest.getContent());
    }

    @PutMapping("/{postId}")
    public ResponseEntity<?> updatePost(@PathVariable Long postId, @RequestBody Map<String, Object> updateData) {
        String result = postService.updatePost(postId, updateData);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<?> deletePost(@PathVariable Long postId) {
        String response = postService.deletePost(postId);
        return ResponseEntity.ok(response);
    }

}
