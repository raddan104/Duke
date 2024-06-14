package com.raddan.OldVK.repository;

import com.raddan.OldVK.entity.Friendship;
import com.raddan.OldVK.enums.FriendshipStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendshipRepository extends JpaRepository<Friendship, Long> {
    Optional<Friendship> findByUser1IdAndUser2Id(Long user1Id, Long user2Id);
    Optional<Friendship> findByUser1IdAndUser2IdAndStatus(Long user1Id, Long user2Id, FriendshipStatus status);
    Optional<List<Friendship>> findAllByUser1IdOrUser2Id(Long user1Id, Long user2Id);
    Optional<List<Friendship>> findAllByUser1IdAndStatus(Long userId, FriendshipStatus status);
    Optional<List<Friendship>> findAllByUser2IdAndStatus(Long userId, FriendshipStatus status);
    void deleteByUser1IdAndUser2Id(Long user1Id, Long user2Id);
}
