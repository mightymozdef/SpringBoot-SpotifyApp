import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class SpotifyService {
  private headers = new HttpHeaders({
    'Content-Type': 'application/json',
    Accept: 'application/json',
  });

  private options = {
    headers: this.headers,
  };

  private apiUrl = 'http://localhost:8888/api';
  constructor(private httpClient: HttpClient) {}

  getSpotifyLoginPage(): Observable<any> {
    const headers = new HttpHeaders().set(
      'Content-Type',
      'text/plain; charset=utf-8'
    );
    const url = `${this.apiUrl}/login`;
    return this.httpClient.get(url, { headers, responseType: 'text' });
  }

  getTopArtists(): Observable<any> {
    const url = `${this.apiUrl}/user-top-artists`;
    return this.httpClient.get(url, this.options);
  }

  getPlaylists(): Observable<any> {
    const url = `${this.apiUrl}/user-playlists`;
    return this.httpClient.get(url, this.options);
  }

  getPlaylistTracks(playlistId: string): Observable<any> {
    const url = `${this.apiUrl}/${playlistId}/tracks`;
    return this.httpClient.get(url, this.options);
  }

  getUserProfile(): Observable<any> {
    const url = `${this.apiUrl}/user-profile`;
    return this.httpClient.get(url, this.options);
  }
}
