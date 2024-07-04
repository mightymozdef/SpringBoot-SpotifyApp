import { Component, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { SpotifyService } from 'src/app/services/spotify-service.service';
import {
  MatTable,
  MatTableDataSource,
  MatTableModule,
} from '@angular/material/table';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { faClock, faUserCircle } from '@fortawesome/free-regular-svg-icons';

@Component({
  selector: 'app-playlist-tracks',
  standalone: true,
  imports: [MatTableModule, FontAwesomeModule],
  templateUrl: './playlist-tracks.component.html',
  styleUrl: './playlist-tracks.component.scss',
})
export class PlaylistTracksComponent {
  playlistTracks: any[] = [];
  tableTracks: any[] = [];
  playlistTrackDurations: number[] = [];
  playlistInformation: any[] = [];
  playlistImageURL = '';
  playlistDescription = '';
  playlistTracksTotal = 0;
  playlistTotalTime = 0;
  playlistOwnerInfo: any[] = [];
  userInformation: any[] = [];
  dataSource = new MatTableDataSource<any>();
  faClock = faClock;
  faUserCircle = faUserCircle;
  displayedColumns: string[] = [
    'trackNumber',
    'title',
    'artist',
    'album',
    'duration',
  ];

  @ViewChild(MatTable) table!: MatTable<any>;

  constructor(
    private spotifyService: SpotifyService,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id') ?? '';

    this.spotifyService.getUserProfile().subscribe((user) => {
      this.userInformation.push(user);
    });

    if (id !== '') {
      this.spotifyService.getPlaylist(id).subscribe((p) => {
        this.playlistDescription = p.description;
        this.playlistInformation.push(p);
        this.playlistImageURL = this.playlistInformation[0].images[0].url;
        this.playlistTracksTotal = p.tracks.total;

        console.log(p);

        this.spotifyService.getUser(p.owner.id).subscribe((user) => {
          console.log(user);
          this.playlistOwnerInfo.push(user);
        });
      });
      this.spotifyService.getPlaylistTracks(id).subscribe((t) => {
        console.log(t);
        this.playlistTracks.push(t);
        let trackNumberCounter = 1;
        for (let song of t) {
          this.playlistTrackDurations.push(song.track.durationMs);
          this.playlistTotalTime += song.track.durationMs;
          if (song.track.type === 'EPISODE') {
            //these are not songs, but episodes added by users (podcasts for example)
            this.tableTracks.push({
              trackNumber: trackNumberCounter,
              name: song.track.name,
              artist: '',
              album: '',
              duration: this.convertSongMsToTime(song.track.durationMs),
            });
          } else {
            // if (song.track.album.images[0]?.url === undefined) {
            //   console.log(song.track);
            // }
            this.tableTracks.push({
              trackNumber: trackNumberCounter,
              name: song.track.name,
              artist: song.track.artists[0].name,
              album: song.track.album.name,
              albumArt: song.track.album.images[0].url,
              duration: this.convertSongMsToTime(song.track.durationMs),
            });
          }
          trackNumberCounter++;
        }
        this.dataSource.data = this.tableTracks;
        this.table.renderRows();
      });
    }
  }

  //needed to convert the total playlist duration (each track added up) to be displayed correctly
  convertMsToTime = (ms: number): string => {
    const totalSeconds = ms / 1000; //convert ms to s
    const hours = Math.floor(totalSeconds / 3600);
    const minutes = Math.floor((totalSeconds % 3600) / 60);
    const seconds = Math.floor(totalSeconds % 60);
    const formattedHrs = hours < 10 ? '0' + hours : hours.toString();
    const formattedMins = minutes < 10 ? '0' + minutes : minutes.toString();
    const formattedSecs = seconds < 10 ? '0' + seconds : seconds.toString();
    return `${formattedHrs} hr ${formattedMins} min ${formattedSecs} sec`;
  };

  convertSongMsToTime = (ms: number): string => {
    const totalSeconds = ms / 1000; //convert ms to s
    const hours = Math.floor(totalSeconds / 3600);
    const minutes = Math.floor((totalSeconds % 3600) / 60);
    const seconds = Math.floor(totalSeconds % 60);
    const formattedHrs = hours < 10 ? '0' + hours : hours.toString();
    const formattedMins = minutes < 10 ? '0' + minutes : minutes.toString();
    const formattedSecs = seconds < 10 ? '0' + seconds : seconds.toString();
    return hours > 0
      ? `${formattedHrs}:${formattedMins}:${formattedSecs}`
      : `${formattedMins}:${formattedSecs}`;
  };
}
