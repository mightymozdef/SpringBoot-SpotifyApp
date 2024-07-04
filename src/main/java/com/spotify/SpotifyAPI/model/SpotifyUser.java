package com.spotify.SpotifyAPI.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import se.michaelthelin.spotify.enums.ModelObjectType;

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
    private ModelObjectType type;
    private String href;
    private String uri;
    private String displayName;

    public SpotifyUser(String userId, String refreshToken, ModelObjectType type, String href, String uri, String displayName) {
        this.userId = userId;
        this.refreshToken = refreshToken;
        this.type = type;
        this.href = href;
        this.uri = uri;
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return "SpotifyUser{" +
                "userId='" + userId + '\'' +
                ", refreshToken='" + refreshToken + '\'' +
                ", type='" + type + '\'' +
                ", href='" + href + '\'' +
                ", uri='" + uri + '\'' +
                ", displayName='" + displayName + '\'' +
                '}';
    }
}