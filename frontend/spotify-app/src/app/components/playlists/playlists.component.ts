import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { SpotifyService } from 'src/app/services/spotify-service.service';

@Component({
  selector: 'app-playlists',
  templateUrl: './playlists.component.html',
  styleUrls: ['./playlists.component.scss'],
})
export class PlaylistsComponent implements OnInit {
  constructor(private spotifyService: SpotifyService, private router: Router) {}

  playlists: any[] = [];

  ngOnInit(): void {
    this.spotifyService.getPlaylists().subscribe((playlists) => {
      playlists.forEach((p: any) => {
        // checking to see if the playlist has an owner with null images
        this.spotifyService.getUser(p.owner.id).subscribe((user) => {
          console.log(
            '-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------'
          );
          console.log('user');
          console.log(user);
          console.log(user.images[0]?.url || 'No image found');
          if (user.images[0]?.url !== null || undefined) {
            console.log('no image found for the following user');
            console.log(user);
          }
          console.log('playlist');
          console.log(p);
        });
        if (p.images !== null) {
          this.playlists.push(p);
        }
      });
    });
  }

  //TODO: need to route this code to the playlist-tracks component and implement pagination
  //      due to spotify limiting api calls to 100 tracks (?)
  logPlaylist(playlist: any) {
    console.log(playlist);
    console.log(
      '---------------------------------------------------------------------------------'
    );
    // const playlistTracks: any[] = [];
    // this.spotifyService.getPlaylistTracks(playlist.id).subscribe((p) => {
    //   playlistTracks.push(p);
    //   console.log(playlistTracks);
    // });
    this.router.navigate(['/playlists', playlist.id]);
  }
}
