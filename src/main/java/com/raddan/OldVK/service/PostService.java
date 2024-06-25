package com.raddan.OldVK.service;

import com.raddan.OldVK.entity.Post;
import com.raddan.OldVK.entity.User;
import com.raddan.OldVK.exception.custom.PostNotFoundException;
import com.raddan.OldVK.exception.custom.UnauthorizedException;
import com.raddan.OldVK.exception.custom.UserNotFoundException;
import com.raddan.OldVK.repository.PostRepository;
import com.raddan.OldVK.repository.UserRepository;
import io.jsonwebtoken.JwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PostService {

    private static final Logger log = LoggerFactory.getLogger(PostService.class);

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    public Post getPostById(Long postId) {
        Optional<Post> optionalPost = postRepository.findById(postId);
        if (optionalPost.isPresent()) {
            return optionalPost.get();
        } else {
            throw new PostNotFoundException("No such post with ID: " + postId);
        }
    }

    public List<Long> getAllPosts() {
        Long authorId = userService.getIdFromJwt();
        try {
            Optional<List<Post>> optionalPosts = postRepository.findAllByAuthorId(authorId);
            if (optionalPosts.isPresent()) {
                List<Post> posts = optionalPosts.get();
                return posts.stream().map(Post::getId).collect(Collectors.toList());
            } else {
                throw new PostNotFoundException("This user didn't posted anything yet!");
            }
        } catch (PostNotFoundException e) {
            return Collections.emptyList();
        }
    }

    public String createPost(String content) {
        Post post = new Post();
        Long authorId = userService.getIdFromJwt();
        try {
            Optional<User> optionalUser = userRepository.findById(authorId);
            if (optionalUser.isPresent()) {
                post.setContent(content);
                post.setAuthor(optionalUser.get());
                post.setCreatedAt(LocalDateTime.now());
                postRepository.save(post);
                return "Post successfully created!";
            } else {
                throw new UserNotFoundException("User not found with ID: " + authorId);
            }
        } catch (UserNotFoundException e) {
            return e.getMessage();
        }
    }

    public String updatePost(Long postId, Map<String, Object> updatedData) {
        Optional<Post> optionalPost = postRepository.findById(postId);

        return optionalPost.map(post -> {
            try {
                for (Map.Entry<String, Object> entry : updatedData.entrySet()) {
                    String fieldName = entry.getKey();
                    Object fieldValue = entry.getValue();

                    Field field = Post.class.getDeclaredField(fieldName);
                    field.setAccessible(true);

                    field.set(post, fieldValue);
                }
                post.setUpdatedAt(LocalDateTime.now());
                postRepository.save(post);
                log.info("Post {} updated successfully!", postId);
                return String.format("Post with ID: '%s' updated successfully!", postId);
            } catch (Exception e) {
                log.error(e.getMessage());
                return "OUCH! Some of your field is broken!";
            }
        }).orElse("Post not found!");
    }

    public String deletePost(Long postId) {
        User authorizedUser = userRepository.findByUsername(userService.getUsernameFromJwt())
                .orElseThrow(() -> {
                    String errorMessage = "Unauthorized.";
                    log.error(errorMessage);
                    return new JwtException(errorMessage);
                });

        return postRepository.findById(postId)
                .map(post -> {
                    User author = post.getAuthor();
                    if (authorizedUser.getId().equals(author.getId())) {
                        postRepository.deleteById(postId);
                        String successMessage = String.format("Post '%s' deleted successfully", postId);
                        log.info(successMessage);
                        return successMessage;
                    } else {
                        String unauthorizedMessage = "Unauthorized attempt to delete post with ID " + postId;
                        log.error(unauthorizedMessage);
                        throw new UnauthorizedException(unauthorizedMessage);
                    }
                })
                .orElseThrow(() -> {
                    String notFoundMessage = "Post not found";
                    log.error("Post {} not found", postId);
                    return new PostNotFoundException(notFoundMessage);
                });
    }


}
