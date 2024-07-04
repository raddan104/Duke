package com.raddan.OldVK.service;

import com.raddan.OldVK.entity.Profile;
import com.raddan.OldVK.entity.User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ProfileService {

    public static Profile createProfile(User user) {
        Profile profile = new Profile();
        profile.setUser(user);
        profile.setUpdatedAt(LocalDateTime.now());
        return profile;
    }
}
