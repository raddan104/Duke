package com.raddan.OldVK.repository;

import com.raddan.OldVK.entity.Friendship;
import com.raddan.OldVK.entity.User;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendshipRepository extends JpaRepository<Friendship, Long> {
    @Query("SELECT f FROM Friendship f WHERE f.friendshipId = :friendshipId")
    Optional<Friendship> findFriendshipById(@Param("friendshipId") Long friendshipId);

    @Query("SELECT f FROM Friendship f WHERE f.user1 = :user OR f.user2 = :user")
    List<Friendship> findAllByUser(@Param("user") User user);

//    @Query("SELECT CASE WHEN COUNT(f) > 0 THEN true ELSE false END FROM Friendship f " +
//            "WHERE (f.user1 = :user1 AND f.user2 = :user2) OR (f.user1 = :user2 AND f.user2 = :user1)")
//    boolean isExistsByUser1AndUser2(@Param("user1") User user1, @Param("user2") User user2);
}
