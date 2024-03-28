package com.spotify.SpotifyAPI.service;

import com.spotify.SpotifyAPI.repository.SpotifyUserRepository;
import com.spotify.SpotifyAPI.model.SpotifyUser;
import java.util.NoSuchElementException;
import org.springframework.stereotype.Service;

@Service
public class SpotifyService {

    private final SpotifyUserRepository spotifyUserRepository;

    public SpotifyService(SpotifyUserRepository spotifyUserRepository) {
        this.spotifyUserRepository = spotifyUserRepository;
    }

    public String getRefreshToken(String userId) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        
        return spotifyUserRepository.findById(userId)
            .map(SpotifyUser::getRefreshToken)
            .orElseThrow(() -> new NoSuchElementException("User not found"));
    }

}