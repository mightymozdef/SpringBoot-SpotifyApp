package com.spotify.SpotifyAPI.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
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
public class SpotifyArtist implements Serializable {

    @Id
    @Column(unique = true)
    private String artistId;
    private String name;
    private String type;
    private String uri;
    private String href;

    @ManyToMany(mappedBy = "artists")
    private Set<SpotifyAlbum> albums = new HashSet<>();

}
