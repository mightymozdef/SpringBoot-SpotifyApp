import { Component } from '@angular/core';
import { SpotifyService } from '../../services/spotify-service.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss'],
})
export class LoginComponent {
  constructor(private spotifyService: SpotifyService) {}

  getSpotifyLoginPage = () => {
    this.spotifyService.getSpotifyLoginPage().subscribe((response) => {
      // response = response.text()
      window.location.replace(response);
    });
  };
}
