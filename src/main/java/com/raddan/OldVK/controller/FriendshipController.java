package com.raddan.OldVK.controller;

import com.raddan.OldVK.service.FriendshipService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/friends")
public class FriendshipController {

    private final FriendshipService friendshipService;

    public FriendshipController(FriendshipService friendshipService) {
        this.friendshipService = friendshipService;
    }

    @GetMapping
    public ResponseEntity<?> getAllFriends(Authentication authentication) {
        return friendshipService.getAllFriends(authentication);
    }

    @PostMapping(path = "/{friendUsername}")
    public ResponseEntity<?> sendFriendRequest(Authentication authentication,
                                               @PathVariable String friendUsername) {
        return friendshipService.sendFriendRequest(authentication, friendUsername);
    }

    @PatchMapping(path = "/requests/{friendshipId}")
    public ResponseEntity<?> acceptFriendRequest(Authentication authentication,
                                                 @PathVariable Long friendshipId) {
        return friendshipService.acceptFriendRequest(authentication, friendshipId);
    }

}
