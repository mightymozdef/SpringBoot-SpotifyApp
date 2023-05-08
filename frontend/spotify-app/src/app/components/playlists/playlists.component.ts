import { Component, OnInit } from '@angular/core';
import { SpotifyService } from 'src/app/services/spotify-service.service';

@Component({
  selector: 'app-playlists',
  templateUrl: './playlists.component.html',
  styleUrls: ['./playlists.component.scss']
})
export class PlaylistsComponent implements OnInit {

  constructor(private spotifyService: SpotifyService) { }

  playlists: any[] = [];

  ngOnInit(): void {
    this.spotifyService.getPlaylists().subscribe((playlists) => {
      playlists.forEach((p: any) => {
        console.log(p)
        this.playlists.push(p)
      })
    })
    console.log(this.playlists)
  }


}
