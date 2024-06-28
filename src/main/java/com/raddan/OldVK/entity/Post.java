package com.raddan.OldVK.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "posts")
@Getter @Setter
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Post implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long postID;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference
    private User user;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "mediatype", length = 50)
    private String mediaType;

    @Column(name = "mediaurl", length = 255)
    private String mediaURL;

    @Column(name = "timestamp", nullable = false, updatable = false)
    private LocalDateTime timestamp;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Like> likes = new ArrayList<>();

    public Post() {
        this.timestamp = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "Post{" +
                "postID=" + postID +
                ", user=" + user +
                ", content='" + content + '\'' +
                ", mediaType='" + mediaType + '\'' +
                ", mediaURL='" + mediaURL + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Post post)) return false;

        return postID.equals(post.postID)
                && user.equals(post.user)
                && Objects.equals(content, post.content)
                && Objects.equals(mediaType, post.mediaType)
                && Objects.equals(mediaURL, post.mediaURL)
                && timestamp.equals(post.timestamp);
    }

    @Override
    public int hashCode() {
        int result = postID.hashCode();
        result = 31 * result + user.hashCode();
        result = 31 * result + Objects.hashCode(content);
        result = 31 * result + Objects.hashCode(mediaType);
        result = 31 * result + Objects.hashCode(mediaURL);
        result = 31 * result + timestamp.hashCode();
        return result;
    }

}
