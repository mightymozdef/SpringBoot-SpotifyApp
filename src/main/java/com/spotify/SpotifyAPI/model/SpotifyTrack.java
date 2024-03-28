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
public class SpotifyTrack implements Serializable {

    @Id
    @Column(unique = true)
    private String trackId;
    private String name;
    private String type;
    private String uri;
    private String previewUrl;

    @OneToMany(mappedBy = "trackId")
    private Set<SpotifyPlaylistTracks> playlists = new HashSet<>();

    @ManyToMany
    @JoinTable(name = "track_artist",
    joinColumns = @JoinColumn(name = "track_id", referencedColumnName = "trackId"),
    inverseJoinColumns = @JoinColumn(name = "artist_id", referencedColumnName = "artistId"))
    private Set<SpotifyArtist> artists = new HashSet<>();

}
