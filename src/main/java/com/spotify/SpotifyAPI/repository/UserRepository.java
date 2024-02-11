package com.spotify.SpotifyAPI.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.spotify.SpotifyAPI.model.User;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

}