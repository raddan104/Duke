package com.raddan.OldVK.service;

import com.raddan.OldVK.entity.Profile;
import com.raddan.OldVK.entity.User;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class ProfileService {

    public static Profile createProfile(User user) {
        Profile profile = new Profile();
        profile.setUser(user);
        profile.setUpdatedAt(LocalDate.now());
        return profile;
    }
}
