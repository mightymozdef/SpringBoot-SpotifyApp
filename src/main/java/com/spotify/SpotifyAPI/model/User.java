package com.spotify.SpotifyAPI.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class User implements Serializable {

    @Id
    private String id;

    private String refreshToken;

    public User(String id, String refreshToken) {
        this.id = id;
        this.refreshToken = refreshToken;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", refreshToken='" + refreshToken + '\'' +
                '}';
    }
}