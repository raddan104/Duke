package com.raddan.OldVK.repository;

import com.raddan.OldVK.entity.Profile;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, Long> {
    @Query("SELECT p FROM Profile p WHERE p.user.id = :userId")
    Optional<Profile> findByUserId(@Param("userId") Long userId);
}
