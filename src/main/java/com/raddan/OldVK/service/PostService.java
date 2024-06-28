package com.raddan.OldVK.service;

import com.raddan.OldVK.dto.PostDTO;
import com.raddan.OldVK.entity.Post;
import com.raddan.OldVK.entity.User;
import com.raddan.OldVK.repository.PostRepository;
import com.raddan.OldVK.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Service
public class PostService {

    private final Logger logger = LoggerFactory.getLogger(PostService.class);

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public PostService(PostRepository postRepository, UserRepository userRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    public ResponseEntity<?> getPost(Long postID) {
        try {
            Post post = postRepository.findByPost_Id(postID)
                    .orElseThrow(() -> new RuntimeException("Post not found!"));

            Map<String, String> postInfo = new ConcurrentHashMap<>();
            for (Field field : Post.class.getDeclaredFields()) {
                field.setAccessible(true);

                if (field.getName().equals("user")) {
                    User user = post.getUser();
                    String username = user.getUsername();
                    postInfo.put(field.getName(), username);
                } else {
                    Object value = field.get(post);
                    String valueAsString = (value != null) ? value.toString() : "null";
                    postInfo.put(field.getName(), valueAsString);
                }
            }
            return ResponseEntity.ok(postInfo);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public ResponseEntity<?> createPost(Authentication authentication, PostDTO postDTO) throws SQLException {
        try {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            User authorizedUser = userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException(userDetails.getUsername() + " not found"));

            Post post = new Post();
            post.setContent(postDTO.content().trim());
            post.setUser(authorizedUser);
            postDTO.mediaType().ifPresent(mediaType -> post.setMediaType(mediaType.trim()));
            postDTO.mediaUrl().ifPresent(mediaUrl -> post.setMediaURL(mediaUrl.trim()));
            postRepository.save(post);

            return ResponseEntity.ok("Post created successfully with ID: " + post.getPostID());
        } catch (RuntimeException e) {
            logger.error("Can't create post: {}", e.getMessage());
            SQLException sqlException = new SQLException(e.getMessage());
            sqlException.initCause(e.getCause());
            throw sqlException;
        }
    }

    public ResponseEntity<?> updatePost(Authentication authentication,
                                        Map<String, Object> updatedData,
                                        Long postID) throws SQLException {
        try {
            Post post = postRepository.findByPost_Id(postID)
                    .orElseThrow(() -> new RuntimeException("Post not found!"));

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            User authorizedUser = userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException(userDetails.getUsername() + " not found"));

            if (!post.getUser().getUserID().equals(authorizedUser.getUserID())) {
                return ResponseEntity.status(FORBIDDEN).body("You can't edit this post!");
            }

            updatedData.forEach((fieldName, newValue) -> {
                try {
                    Field field = post.getClass().getDeclaredField(fieldName);
                    field.setAccessible(true);
                    field.set(post, newValue);
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    logger.error("Error with updating field '{}' : {}", fieldName, e.getMessage());
                }
            });

            postRepository.save(post);
            return ResponseEntity.ok(String.format("Post with id '%s', successfully updated!", postID));
        } catch (RuntimeException e) {
            logger.error("Can't update post: {}", e.getMessage());
            SQLException sqlException = new SQLException(e.getMessage());
            sqlException.initCause(e.getCause());
            throw sqlException;
        }
    }

    public ResponseEntity<?> deletePost(Authentication authentication,
                                        Long postID) {
        Post post = postRepository.findByPost_Id(postID)
                .orElseThrow(() -> new RuntimeException("Post not found with ID: " + postID));

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User authorizedUser = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException(userDetails.getUsername() + " not found"));

        if (!post.getUser().getUserID().equals(authorizedUser.getUserID())) {
            return ResponseEntity.status(FORBIDDEN).body("You can't delete this post!");
        }

        try {
            postRepository.delete(post);
            return ResponseEntity.ok(String.format("Post '%s' deleted.", postID));
        } catch (RuntimeException e) {
            logger.error("Can't delete post with ID: {}. {}", postID, e.getMessage());
            return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                    .body(String.format("Can't delete post with ID: '%s'. '%s'", postID, e.getMessage()));
        }
    }
}
