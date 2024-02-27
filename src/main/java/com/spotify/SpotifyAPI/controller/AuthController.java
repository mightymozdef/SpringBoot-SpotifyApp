package com.spotify.SpotifyAPI.controller;

import com.spotify.SpotifyAPI.constant.Keys;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.*;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.SpotifyHttpManager;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import se.michaelthelin.spotify.model_objects.specification.*;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeRefreshRequest;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeRequest;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeUriRequest;
import se.michaelthelin.spotify.requests.data.personalization.simplified.GetUsersTopArtistsRequest;
import se.michaelthelin.spotify.requests.data.playlists.GetListOfCurrentUsersPlaylistsRequest;
import se.michaelthelin.spotify.requests.data.playlists.GetPlaylistRequest;
import se.michaelthelin.spotify.requests.data.playlists.GetPlaylistsItemsRequest;
import se.michaelthelin.spotify.requests.data.users_profile.GetCurrentUsersProfileRequest;

import org.apache.hc.core5.http.ParseException;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

@RestController
@RequestMapping("/api")
public class AuthController {

    private static final URI redirectUri = SpotifyHttpManager.makeUri("http://localhost:8888/api/get-user-code");


    private static final SpotifyApi spotifyApi = new SpotifyApi.Builder()
            .setClientId(Keys.CLIENT_ID.label)
            .setClientSecret(Keys.CLIENT_SECRET.label)
            .setRedirectUri(redirectUri)
            .build();

    private static final AuthorizationCodeRefreshRequest authorizationCodeRefreshRequest = spotifyApi.authorizationCodeRefresh().build();

    @GetMapping("/login")
    @ResponseBody
    public String spotifyLogin() {
        AuthorizationCodeUriRequest authorizationCodeUriRequest = spotifyApi.authorizationCodeUri()
                .scope("user-read-private,user-read-email,user-top-read,playlist-read-private,playlist-read-collaborative")
                .show_dialog(true)
                .build();
        final URI uri = authorizationCodeUriRequest.execute();
        return uri.toString();
    }

    @GetMapping("/get-user-code")
    public String getSpotifyUserCode(@RequestParam("code") String userCode, HttpServletResponse response) throws IOException {

        AuthorizationCodeRequest authorizationCodeRequest = spotifyApi.authorizationCode(userCode).build();

        try {
            final AuthorizationCodeCredentials authorizationCodeCredentials = authorizationCodeRequest.execute();

            //  set access and refresh token for further SpotifyAPI object usage
            spotifyApi.setAccessToken(authorizationCodeCredentials.getAccessToken());
            spotifyApi.setRefreshToken(authorizationCodeCredentials.getRefreshToken());

            System.out.println("Expires in: " + authorizationCodeCredentials.getExpiresIn());

            final User spotifyUser = spotifyApi.getCurrentUsersProfile().build().execute();

            com.spotify.SpotifyAPI.model.User user = new com.spotify.SpotifyAPI.model.User(spotifyUser.getId(), spotifyApi.getRefreshToken());
            System.out.println(user);

        } catch(IOException | SpotifyWebApiException | ParseException e) {
            System.out.println("Error: " + e.getMessage());

            if(e.getMessage().contains("access token")){
                System.out.println("access token refresh");
                authorizationCodeRefresh();
            }

        }
        response.sendRedirect("http://localhost:4200/top-artists");
        return spotifyApi.getAccessToken();
    }

    public void authorizationCodeRefresh() {
        try {
            final CompletableFuture<AuthorizationCodeCredentials> authorizationCodeCredentialsFuture = authorizationCodeRefreshRequest.executeAsync();

            final AuthorizationCodeCredentials authorizationCodeCredentials = authorizationCodeCredentialsFuture.join();

            spotifyApi.setAccessToken(authorizationCodeCredentials.getAccessToken());

            System.out.println("Expires in: " + authorizationCodeCredentials.getExpiresIn());

        } catch (CompletionException e) {
            System.out.println(e.getCause().getMessage());
        } catch (CancellationException e){
            System.out.println("Async operation cancelled");
        }
    }

    @GetMapping("/user-profile")
    public User getUserProfile() {
        final GetCurrentUsersProfileRequest getCurrentUsersProfileRequest = spotifyApi.getCurrentUsersProfile().build();

        try {
            return getCurrentUsersProfileRequest.execute();
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return null; //user not found
    }

    @GetMapping("/user-top-artists")
    public Artist[] getUserTopArtists() {

        final GetUsersTopArtistsRequest getUsersTopArtistsRequest = spotifyApi.getUsersTopArtists()
                .time_range("long_term")
                .build();

        try {
            final Paging<Artist> artistPaging = getUsersTopArtistsRequest.execute();

            //return top artists as JSON
            return artistPaging.getItems();
        } catch(Exception e){
            System.out.println("Something went wrong!\n" + e.getMessage());
        }
        return new Artist[0];
    }

    @GetMapping("/user-playlists")
    public PlaylistSimplified[] getUserPlaylists() {

        final GetListOfCurrentUsersPlaylistsRequest getListOfUsersPlaylistsRequest = spotifyApi.getListOfCurrentUsersPlaylists().build();

        try {
            final Paging<PlaylistSimplified> playlistSimplifiedPaging = getListOfUsersPlaylistsRequest.execute();

            //return a list of the current user's playlists
            return playlistSimplifiedPaging.getItems();

        } catch(IOException | SpotifyWebApiException | ParseException e) {
            System.out.println("Error: " + e.getMessage());
        }

        return new PlaylistSimplified[0];
    }

    @GetMapping("/{playlistId}/tracks")
    public PlaylistTrack[] getPlaylistTracks(@PathVariable String playlistId) {

        final GetPlaylistsItemsRequest getPlaylistItemsRequest = spotifyApi.getPlaylistsItems(playlistId).build();
        try {
            final Paging<PlaylistTrack> playlistTrackPaging = getPlaylistItemsRequest.execute();
            //return a list of the current user's playlists
            return playlistTrackPaging.getItems();

        } catch(IOException | SpotifyWebApiException | ParseException e) {
            System.out.println("Error: " + e.getMessage());
        }

        return new PlaylistTrack[0];
    }
}
