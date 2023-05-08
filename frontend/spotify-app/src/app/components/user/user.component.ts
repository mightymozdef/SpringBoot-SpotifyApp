import { Component, OnInit } from '@angular/core';
import { SpotifyService } from 'src/app/services/spotify-service.service';

@Component({
  selector: 'app-user',
  templateUrl: './user.component.html',
  styleUrls: ['./user.component.scss']
})
export class UserComponent implements OnInit {


  constructor(private spotifyService: SpotifyService) { }


  user: any;

  ngOnInit(): void {
    this.spotifyService.getUserProfile().subscribe((user) => {
      console.log(user)

      this.user = JSON.stringify(user);

    })
  }

}
