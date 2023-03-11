package com.spotify.SpotifyAPI.repository;

import com.spotify.SpotifyAPI.security.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    public User findByUsername(String username);


}
