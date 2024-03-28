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
public class SpotifyPlaylistTracks implements Serializable {

    @Id
    @ManyToOne
    @JoinColumn(name = "playlistId", referencedColumnName = "playlistId")
    private SpotifyPlaylist playlistId;

    @Id
    @ManyToOne
    @JoinColumn(name = "trackId", referencedColumnName = "trackId")
    private SpotifyTrack trackId;
    private int trackNumber; //the track number within the playlist (keep order of songs)
}
