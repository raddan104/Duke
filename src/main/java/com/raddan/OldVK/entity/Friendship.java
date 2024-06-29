package com.raddan.OldVK.entity;

import com.raddan.OldVK.enums.FriendshipStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

import static com.raddan.OldVK.enums.FriendshipStatus.*;

@Entity
@Table(name = "friendships")
@Getter @Setter
public class Friendship implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "friendship_id", nullable = false)
    private Long friendshipId;

    @ManyToOne
    @JoinColumn(name = "user1_id", nullable = false)
    private User user1;

    @ManyToOne
    @JoinColumn(name = "user2_id", nullable = false)
    private User user2;

    @Column(nullable = false)
    private LocalDate timestamp;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private FriendshipStatus status;

    public Friendship() {
        this.status = PENDING;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Friendship that)) return false;
        return Objects.equals(friendshipId, that.friendshipId) && Objects.equals(user1, that.user1) && Objects.equals(user2, that.user2) && Objects.equals(timestamp, that.timestamp) && status == that.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(friendshipId, user1, user2, timestamp, status);
    }

    @Override
    public String toString() {
        return "Friendship{" +
                "friendshipId=" + friendshipId +
                ", user1=" + user1 +
                ", user2=" + user2 +
                ", timestamp=" + timestamp +
                ", status=" + status +
                '}';
    }
}
