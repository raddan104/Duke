package com.raddan.OldVK.repository;

import com.raddan.OldVK.entity.Friendship;
import com.raddan.OldVK.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FriendshipRepository extends JpaRepository<Friendship, Long> {
    List<Friendship> findAllBySenderOrReceiver(User sender, User receiver);
}
