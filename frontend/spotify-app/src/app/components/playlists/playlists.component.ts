import { Component, OnInit } from '@angular/core';
import { SpotifyService } from 'src/app/services/spotify-service.service';

@Component({
  selector: 'app-playlists',
  templateUrl: './playlists.component.html',
  styleUrls: ['./playlists.component.scss'],
})
export class PlaylistsComponent implements OnInit {
  //TODO: need to route this code to the playlist-tracks component and implement pagination
  //      due to spotify limiting api calls to 100 tracks (?)
  logPlaylist(playlist: any) {
    console.log(playlist);
    const playlistTracks: any[] = [];
    this.spotifyService.getPlaylistTracks(playlist.id).subscribe((p) => {
      playlistTracks.push(p);
      console.log(playlistTracks);
    });
  }

  constructor(private spotifyService: SpotifyService) {}

  playlists: any[] = [];

  ngOnInit(): void {
    this.spotifyService.getPlaylists().subscribe((playlists) => {
      playlists.forEach((p: any) => {
        // console.log(p);
        this.playlists.push(p);
      });
    });
  }
}
