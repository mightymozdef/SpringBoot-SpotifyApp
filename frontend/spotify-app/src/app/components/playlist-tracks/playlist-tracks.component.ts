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
  playlistTrackDurations: number[] = [];
  playlistInformation: any[] = [];
  playlistImageURL: string = '';
  playlistDescription: string = '';
  playlistTracksTotal: number = 0;
  playlistTotalTime: number = 0;

  constructor(
    private spotifyService: SpotifyService,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id') ?? '';
    // console.log(id);

    if (id !== '') {
      this.spotifyService.getPlaylist(id).subscribe((p) => {
        // console.log(p);
        this.playlistDescription = p.description;
        this.playlistInformation.push(p);
        this.playlistImageURL = this.playlistInformation[0].images[0].url;
        this.playlistTracksTotal = p.tracks.total;
      });
      this.spotifyService.getPlaylistTracks(id).subscribe((p) => {
        // console.log(p);
        this.playlistTracks.push(p);

        for (let song of p) {
          console.log(song);
          this.playlistTrackDurations.push(song.track.durationMs);
          this.playlistTotalTime += song.track.durationMs;
        }
        console.log(this.playlistTotalTime);
      });
    }
  }

  //needed to convert the total playlist duration (each track added up) to be displayed correctly
  convertMsToTime = (ms: number): string => {
    const totalSeconds = ms / 1000; //convert ms to s
    const hours = Math.floor(totalSeconds / 3600);
    const minutes = Math.floor((totalSeconds % 3600) / 60);
    const seconds = Math.floor((totalSeconds % 3600) / 3600);
    const formattedHrs = hours < 10 ? '0' + hours : hours.toString();
    const formattedMins = minutes < 10 ? '0' + minutes : minutes.toString();
    const formattedSecs = seconds < 10 ? '0' + seconds : seconds.toString();
    return `${formattedHrs} hr ${formattedMins} min ${formattedSecs} sec`;
  };
}
