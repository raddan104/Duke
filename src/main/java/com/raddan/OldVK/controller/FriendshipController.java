package com.raddan.OldVK.controller;

import com.raddan.OldVK.entity.Friendship;
import com.raddan.OldVK.service.FriendshipService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/friends")
@RequiredArgsConstructor
public class FriendshipController {

    private final FriendshipService friendshipService;

    @GetMapping
    public List<String> getAllFriends() {
        return friendshipService.getAllFriends();
    }

    @PostMapping("/request/{friendUsername}")
    public Friendship sendFriendRequest(@PathVariable String friendUsername) {
        return friendshipService.sendFriendRequestByUsername(friendUsername);
    }

    @PostMapping("/accept/{friendshipId}")
    public String acceptFriendRequest(@PathVariable Long friendshipId) {
        return friendshipService.acceptFriendRequest(friendshipId);
    }

    @PostMapping("/decline/{friendshipId}")
    public String declineFriendRequest(@PathVariable Long friendshipId) {
        return friendshipService.declineFriendRequest(friendshipId);
    }


}
