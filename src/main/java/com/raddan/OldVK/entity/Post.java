package com.raddan.OldVK.entity;

import jakarta.persistence.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "posts")
public class Post implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ID;

    @ManyToOne
    @JoinColumn(name = "user", nullable = false)
    private User user;

    @Column(columnDefinition = "text")
    private String content;

    @Column(name = "mediatype", length = 50)
    private String mediaType;

    @Column(name = "mediaurl", length = 255)
    private String mediaURL;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private Set<Like> likes = new HashSet<>();

    public Post() {
        this.createdAt = LocalDateTime.now();
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public void setMediaURL(String mediaURL) {
        this.mediaURL = mediaURL;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public void setLikes(Set<Like> likes) {
        this.likes = likes;
    }

    public Long getID() {
        return ID;
    }

    public User getUser() {
        return user;
    }

    public String getContent() {
        return content;
    }

    public String getMediaType() {
        return mediaType;
    }

    public String getMediaURL() {
        return mediaURL;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public Set<Like> getLikes() {
        return likes;
    }
}
