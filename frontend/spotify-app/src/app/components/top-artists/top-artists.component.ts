import { Component, OnInit } from '@angular/core';
import { SpotifyService } from 'src/app/services/spotify-service.service';

@Component({
  selector: 'app-top-artists',
  templateUrl: './top-artists.component.html',
  styleUrls: ['./top-artists.component.scss'],
})
export class TopArtistsComponent implements OnInit {
  constructor(private spotifyService: SpotifyService) {}

  topArtists: any[] = [];

  ngOnInit(): void {
    this.spotifyService.getUserProfile().subscribe((user) => {
      // this.spotifyService.getUserToken().subscribe((u) => {
      //   console.log('getUserToken');
      //   console.log(u);
      //   console.log(user);
      //   user.refreshToken = u.refreshToken;
      //   //persist user information locally to be used for new user profile page
      localStorage.setItem('user', JSON.stringify(user));
      //   localStorage.setItem('refreshToken', user.refreshToken);
      // });

      this.spotifyService.getUser(user?.id).subscribe((u) => {
        console.log('getUser');
        console.log(u);
        // console.log(JSON.parse(localStorage.getItem('user') ?? '')?.id);
      });
    });

    this.spotifyService.getTopArtists().subscribe((artists) => {
      // this.topArtists = artists

      artists.forEach((a: any) => {
        // console.log(a)
        // console.log(a.images[0]?.url)
        this.topArtists.push(a);
      });

      // console.log(this.topArtists)
    });
  }
}
