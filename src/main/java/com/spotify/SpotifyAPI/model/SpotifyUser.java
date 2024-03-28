package com.spotify.SpotifyAPI.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Entity
@NoArgsConstructor
@Getter
@Setter
@Table
public class SpotifyUser implements Serializable {

    @Id
    @Column(unique = true)
    private String userId;
    private String refreshToken;
    private String type;
    private String href;
    private String uri;
    private String displayName;

    public SpotifyUser(String userId, String refreshToken) {
        this.userId = userId;
        this.refreshToken = refreshToken;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + userId + '\'' +
                ", refreshToken='" + refreshToken + '\'' +
                '}';
    }
}