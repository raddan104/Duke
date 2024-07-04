package com.raddan.OldVK.entity;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "likes")
public class Like {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ID;

    @ManyToOne
    @JoinColumn(name = "post")
    private Post post;

    @ManyToOne
    @JoinColumn(name = "comment")
    private Comment comment;

    @ManyToOne
    @JoinColumn(name = "user")
    private User user;

    public void setPost(Post post) {
        this.post = post;
    }

    public void setComment(Comment comment) {
        this.comment = comment;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }

    public Long getID() {
        return ID;
    }

    public Post getPost() {
        return post;
    }

    public Comment getComment() {
        return comment;
    }

    public User getUser() {
        return user;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    @Column
    private LocalDate createdAt;

}
