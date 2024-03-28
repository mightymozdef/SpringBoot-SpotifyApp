import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { SpotifyService } from 'src/app/services/spotify-service.service';

@Component({
  selector: 'app-playlist-tracks',
  standalone: true,
  imports: [],
  templateUrl: './playlist-tracks.component.html',
  styleUrl: './playlist-tracks.component.scss',
})
export class PlaylistTracksComponent {
  playlistTracks: any[] = [];
  playlistInformation: any[] = [];
  playlistImageURL: string = '';

  constructor(
    private spotifyService: SpotifyService,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id') ?? '';
    console.log(id);

    if (id !== '') {
      this.spotifyService.getPlaylist(id).subscribe((p) => {
        console.log(p);
        this.playlistInformation.push(p);
        this.playlistImageURL = this.playlistInformation[0].images[0].url;
      });
      this.spotifyService.getPlaylistTracks(id).subscribe((p) => {
        this.playlistTracks.push(p);
        console.log(this.playlistTracks);
      });
    }
  }
}
