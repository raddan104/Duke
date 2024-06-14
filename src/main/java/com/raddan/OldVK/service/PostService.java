package com.raddan.OldVK.service;

import com.raddan.OldVK.entity.Post;
import com.raddan.OldVK.entity.User;
import com.raddan.OldVK.exception.DeletionException;
import com.raddan.OldVK.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PostService {

    private final PostRepository postRepository;

    @Autowired
    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public Optional<Post> getPostById(Long postId) {
        return postRepository.findById(postId);
    }

    public Post createPost(String content, User author) {
        Post post = new Post();
        post.setContent(content);
        post.setAuthor(author);
        return postRepository.save(post);
    }

    public Post updatePost(Post post) {
        Optional<Post> existingPost = postRepository.findById(post.getId());
        if (existingPost.isPresent()) {
            existingPost.get().setContent(post.getContent());
            return postRepository.save(existingPost.get());
        }
        throw new IllegalArgumentException("Post with ID: " + post.getId() + " not found!");
    }

    public void deletePost(Long postId) {
        try {
            postRepository.deleteById(postId);
        } catch (RuntimeException e) {
            String message = "Failed to delete post with ID: " + postId;
            String cause = e.getMessage();
            throw new DeletionException(message + "\nCause: " + cause, e);
        }
    }
}
