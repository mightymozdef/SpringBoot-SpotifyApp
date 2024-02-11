package com.spotify.SpotifyAPI.service;

import com.spotify.SpotifyAPI.repository.UserRepository;
import com.spotify.SpotifyAPI.model.User;
import org.springframework.stereotype.Service;

@Service
public class SpotifyService {


    private UserRepository userRepository;

    public SpotifyService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void saveRefreshToken(String userId, String refreshToken) {
        User user = new User(userId, refreshToken);
        userRepository.save(user);
    }

    public String getRefreshToken(String userId) {
        return userRepository.findById(userId).map(User::getRefreshToken).orElse(null);
    }


}