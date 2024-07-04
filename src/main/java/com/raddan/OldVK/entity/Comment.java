package com.raddan.OldVK.entity;

import jakarta.persistence.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "comments")
public class Comment implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ID;

    @ManyToOne
    @JoinColumn(name = "post")
    private Post post;

    @ManyToOne
    @JoinColumn(name = "user")
    private User user;

    @OneToMany(mappedBy = "comment", cascade = CascadeType.ALL)
    private List<Like> likes = new ArrayList<>();

    @Column
    private String content;

    @Column
    private LocalDate createdAt;

    public Long getID() {
        return ID;
    }

    public Post getPost() {
        return post;
    }

    public User getUser() {
        return user;
    }

    public List<Like> getLikes() {
        return likes;
    }

    public String getContent() {
        return content;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setLikes(List<Like> likes) {
        this.likes = likes;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }
}
