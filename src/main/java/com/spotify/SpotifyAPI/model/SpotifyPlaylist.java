package com.spotify.SpotifyAPI.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class SpotifyPlaylist implements Serializable {

    @Id
    @Column(unique = true)
    private String playlistId;
    private String name;
    private String snapshotId;
    private String description;
    private String href;
    private boolean isCollaborative;
    private boolean isPublicAccess;
    private String type;
    private String uri;

    @ManyToOne
    @JoinColumn(name = "ownerId", referencedColumnName = "userId")
    private SpotifyUser ownerId; //map to the Spotify User table (FK) userId

    @OneToMany(mappedBy = "trackId")
    private Set<SpotifyTrack> tracks = new HashSet<>(); //map playlist to tracks

}
