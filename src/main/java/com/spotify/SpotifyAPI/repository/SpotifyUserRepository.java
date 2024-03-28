package com.spotify.SpotifyAPI.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.spotify.SpotifyAPI.model.SpotifyUser;
import org.springframework.stereotype.Repository;

@Repository
public interface SpotifyUserRepository extends JpaRepository<SpotifyUser, String> {
    SpotifyUser findByUserId(String userId);
}