package com.raddan.OldVK.service;

import com.raddan.OldVK.entity.Friendship;
import com.raddan.OldVK.entity.User;
import com.raddan.OldVK.enums.FriendshipStatus;
import com.raddan.OldVK.repository.FriendshipRepository;
import com.raddan.OldVK.repository.UserRepository;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FriendshipService {

    private final FriendshipRepository friendshipRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    public List<String> getAllFriends() {
        String username = userService.getUsernameFromJwt();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Set<User> friends = user.getFriends();
        if (friends == null || friends.isEmpty()) {
            return Collections.emptyList();
        }

        return friends.stream()
                .map(User::getUsername)
                .collect(Collectors.toList());
    }

    public Map<Long, String> getAllFriendRequests() {
        String username = userService.getUsernameFromJwt();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Friendship> friendRequests = friendshipRepository.findByReceiverAndStatus(user, FriendshipStatus.PENDING);

        if (friendRequests == null)
            return Collections.emptyMap();

        return friendRequests.stream()
                .collect(Collectors.toMap(
                        Friendship::getId,
                        friendship -> friendship.getSender().getUsername(),
                        (existing, replacement) -> existing,
                        ConcurrentHashMap::new)
                );
    }

    public String sendFriendRequestByUsername(String friendUsername) {
        User friend = userRepository.findByUsername(friendUsername)
                .orElseThrow(() -> new UsernameNotFoundException("User with username: " + friendUsername + " not found!"));

        User authorizedUser = userRepository.findById(userService.getIdFromJwt())
                .orElseThrow(() -> new JwtException("Your token expired or illegal!"));

        Friendship friendship = new Friendship();
        friendship.setSender(authorizedUser);
        friendship.setReceiver(friend);
        friendship.setCreatedAt(LocalDateTime.now());
        friendship.setStatus(FriendshipStatus.PENDING);

        friendshipRepository.save(friendship);

        return "You have successfully sent a friend request to a user: " + friendUsername;
    }

    @Transactional
    public String acceptFriendRequest(Long friendshipId) {
        Friendship friendship = friendshipRepository.findById(friendshipId)
                .orElseThrow(() -> new RuntimeException("Friendship request not found"));

        User receiver = userRepository.findById(userService.getIdFromJwt())
                .orElseThrow(() -> new JwtException("Your token expired or illegal!"));

        if (friendship.getStatus() == FriendshipStatus.PENDING && friendship.getReceiver().equals(receiver)) {
            friendship.setStatus(FriendshipStatus.ACCEPTED);

            User sender = friendship.getSender();

            sender.getFriends().add(receiver);
            receiver.getFriends().add(sender);

            userRepository.save(sender);
            userRepository.save(receiver);
            friendshipRepository.save(friendship);

            return sender.getUsername() + " and " + receiver.getUsername() + " are friends now!";
        } else {
            return "You are not allowed to do that!";
        }
    }

    @Transactional
    public String declineFriendRequest(Long friendshipId) {
        Friendship friendship = friendshipRepository.findById(friendshipId)
                .orElseThrow(() -> new RuntimeException("Friendship request not found"));

        User receiver = userRepository.findById(userService.getIdFromJwt())
                .orElseThrow(() -> new JwtException("Your token expired or illegal!"));

        if (friendship.getStatus() == FriendshipStatus.PENDING && friendship.getReceiver().equals(receiver)) {
            friendship.setStatus(FriendshipStatus.DECLINED);

            User sender = friendship.getSender();

            userRepository.save(sender);
            userRepository.save(receiver);
            friendshipRepository.save(friendship);

            return "You declined " + sender.getUsername() + "'s request.";
        } else {
            return "You are not allowed to do that!";
        }
    }

}
