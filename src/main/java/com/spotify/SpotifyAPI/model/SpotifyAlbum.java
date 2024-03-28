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
public class SpotifyAlbum implements Serializable {

    @Id
    @Column(unique = true)
    private String albumId;
    private String name;
    private String uri;
    private String type;

    @ManyToMany
    @JoinTable(
            name = "album_artist",
            joinColumns = @JoinColumn(name = "albumId", referencedColumnName = "albumId"),
            inverseJoinColumns = @JoinColumn(name = "artistId", referencedColumnName = "artistId"))
    private Set<SpotifyArtist> artists = new HashSet<>();

}
