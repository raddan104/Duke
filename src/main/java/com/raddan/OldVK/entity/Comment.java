package com.raddan.OldVK.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;

@Entity
@Table(name = "comments")
@Getter @Setter
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Comment implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long commentID;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    @JsonBackReference
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonBackReference
    private User user;

    private String content;
    private LocalDate timestamp;

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Comment comment)) return false;

        return commentID.equals(comment.commentID) && post.equals(comment.post) && user.equals(comment.user) && content.equals(comment.content) && timestamp.equals(comment.timestamp);
    }

    @Override
    public int hashCode() {
        int result = commentID.hashCode();
        result = 31 * result + post.hashCode();
        result = 31 * result + user.hashCode();
        result = 31 * result + content.hashCode();
        result = 31 * result + timestamp.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "commentID=" + commentID +
                ", post=" + post +
                ", user=" + user +
                ", content='" + content + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
