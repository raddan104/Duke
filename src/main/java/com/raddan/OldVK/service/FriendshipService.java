package com.raddan.OldVK.service;

import com.raddan.OldVK.entity.Friendship;
import com.raddan.OldVK.entity.User;
import com.raddan.OldVK.repository.FriendshipRepository;
import com.raddan.OldVK.repository.UserRepository;
import com.raddan.OldVK.utils.AuthUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.raddan.OldVK.enums.FriendshipStatus.ACCEPTED;
import static org.springframework.http.HttpStatus.*;
import static com.raddan.OldVK.enums.FriendshipStatus.*;

@Service
public class FriendshipService {

    private static final Logger logger = LoggerFactory.getLogger(FriendshipService.class);

    private final FriendshipRepository friendshipRepository;
    private final UserRepository userRepository;

    public FriendshipService(FriendshipRepository friendshipRepository, UserRepository userRepository) {
        this.friendshipRepository = friendshipRepository;
        this.userRepository = userRepository;
    }

    public ResponseEntity<?> getAllFriends(Authentication authentication) {
        try {
            UserDetails userDetails = AuthUtils.getUserDetails(authentication);
            User authorizedUser = userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException(userDetails.getUsername() + " not found"));

            List<Friendship> friendships = friendshipRepository.findAllByUser(authorizedUser);

            return ResponseEntity.ok(friendships.stream()
                    .map(f -> f.getUser1().equals(authorizedUser) ? f.getUser2() : f.getUser1())
                    .collect(Collectors.toList())
            );
        } catch (RuntimeException e) {
            logger.error("Error with getting all friends: {}", e.getMessage());
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body("Failed to get friends");
        }
    }

    public ResponseEntity<?> sendFriendRequest(Authentication authentication, String friendUsername) {
        try {
            UserDetails userDetails = AuthUtils.getUserDetails(authentication);
            User authorizedUser = userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException(userDetails.getUsername() + " not found"));

            User friend = userRepository.findByUsername(friendUsername)
                    .orElseThrow(() -> new RuntimeException(friendUsername + " not found"));

//            boolean requestAlreadyExists = friendshipRepository.isExistsByUser1AndUser2(authorizedUser, friend);
//            if (requestAlreadyExists) {
//                return ResponseEntity.status(FORBIDDEN).body("Request from you to this user already exists!");
//            }

            Friendship friendship = new Friendship();
            friendship.setUser1(authorizedUser);
            friendship.setUser2(friend);

            friendshipRepository.save(friendship);
            return ResponseEntity.ok("Friend request created successfully with ID: " + friendship.getFriendshipId());
        } catch (RuntimeException e) {
            logger.error("Error with sending friend request: {}", e.getMessage());
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body("Failed to send friend request!");
        }
    }

    public ResponseEntity<?> acceptFriendRequest(Authentication authentication, Long friendshipId) {
        try {
            UserDetails userDetails = AuthUtils.getUserDetails(authentication);
            User authorizedUser = userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException(userDetails.getUsername() + " not found"));

            Friendship friendship = friendshipRepository.findFriendshipById(friendshipId)
                    .orElseThrow(() -> new RuntimeException("Friendship not found with ID: " + friendshipId));

            if (friendship.getUser1().getUserID().equals(authorizedUser.getUserID())) {
                return ResponseEntity.status(FORBIDDEN).body("Friend request may accept only receiver.");
            }

            if (friendship.getStatus() == ACCEPTED) {
                return ResponseEntity.status(FORBIDDEN).body("This request already accepted");
            } else if (friendship.getStatus() == DECLINED) {
                return ResponseEntity.status(FORBIDDEN).body("This request already declined");
            } else {
                friendship.setStatus(ACCEPTED);
                friendship.setTimestamp(LocalDate.now());
            }

            friendshipRepository.save(friendship);
            return ResponseEntity.ok("Friend request accepted successfully on: " + LocalDateTime.now());
        } catch (RuntimeException e) {
            logger.error("Error with accepting friend request: {}", e.getMessage());
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body("Failed to accept friend request!");
        }
    }
}
