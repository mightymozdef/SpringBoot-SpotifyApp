package com.spotify.SpotifyAPI.controller;

import com.spotify.SpotifyAPI.constant.Keys;
import com.spotify.SpotifyAPI.model.SpotifyUser;

import com.spotify.SpotifyAPI.repository.SpotifyUserRepository;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.ResponseEntity;
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
import se.michaelthelin.spotify.requests.data.users_profile.GetUsersProfileRequest;

import org.apache.hc.core5.http.ParseException;

import java.io.IOException;
import java.net.URI;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


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
    private final SpotifyUserRepository spotifyUserRepository;

    private Instant tokenExpiryTime;

    private SpotifyUser spotUser;

    private String refreshToken;

    public AuthController(SpotifyUserRepository spotifyUserRepository) {
        this.spotifyUserRepository = spotifyUserRepository;
    }


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

            int expires_in = authorizationCodeCredentials.getExpiresIn();
            System.out.println("Expires in: " + expires_in);
            tokenExpiryTime = Instant.now().plusSeconds(expires_in);

            final User spotifyUser = spotifyApi.getCurrentUsersProfile().build().execute();

            spotUser = new SpotifyUser(spotifyUser.getId(), spotifyApi.getRefreshToken());
            spotifyUserRepository.save(spotUser);

            System.out.printf("Spotify user %s saved to database ", spotUser.getUserId());

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

    public boolean isTokenExpired() {
        return Instant.now().isAfter(tokenExpiryTime);
    }

    @GetMapping("/refresh/{userId}")
    public ResponseEntity<String> refresh(@PathVariable String userId) {
        SpotifyUser user = spotifyUserRepository.findByUserId(userId);
        if(user == null) throw new NoSuchElementException("User not found");
        String refreshToken = user.getRefreshToken();
        if(isTokenExpired()) { 
            String newAccessToken = refreshAccessToken(refreshToken); 
            return ResponseEntity.ok(newAccessToken);
        }
        return ResponseEntity.ok("Token is still valid");
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

    @GetMapping("/user/{userId}")
    public User getUser(@PathVariable String userId) {
        final GetUsersProfileRequest getUsersProfileRequest = spotifyApi.getUsersProfile(userId).build();
        try {
            return getUsersProfileRequest.execute();
        }catch (IOException | SpotifyWebApiException | ParseException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return null;
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

    @GetMapping("/playlist/{playlistId}")
    public Playlist getPlaylist(@PathVariable String playlistId) {
        final GetPlaylistRequest getPlaylistRequest = spotifyApi.getPlaylist(playlistId).build();

        Playlist playlist = null;

        try {
            playlist = getPlaylistRequest.execute();
        } catch (IOException | ParseException | SpotifyWebApiException e) {
            System.out.println("Error while fetching playlist: " + e.getMessage());
        }
        if(playlist == null) throw new NoSuchElementException("Playlist not found");
        return playlist;
    }

    @GetMapping("/{playlistId}/tracks")
    public CompletableFuture<List<PlaylistTrack>> getPlaylistTracks(@PathVariable String playlistId) {

        List<CompletableFuture<Paging<PlaylistTrack>>> futures = new ArrayList<>();
        final int limit = 100; //max limit per call
        int offset = 0;
        try {
            GetPlaylistsItemsRequest getPlaylistsItemsRequest = spotifyApi.getPlaylistsItems(playlistId).limit(limit).offset(offset).build();
            CompletableFuture<Paging<PlaylistTrack>> future = getPlaylistsItemsRequest.executeAsync();
            futures.add(future);
            Paging<PlaylistTrack> playlistTrackPaging = future.join();
            offset += limit;
            while(offset < playlistTrackPaging.getTotal()){
                getPlaylistsItemsRequest = spotifyApi.getPlaylistsItems(playlistId).limit(limit).offset(offset).build();
                future = getPlaylistsItemsRequest.executeAsync();
                futures.add(future);
                playlistTrackPaging = future.join();
                offset += limit;
            }
        } catch(Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(v -> futures.stream()
                        .map(future -> {
                            try {
                                return future.join();
                            } catch(CompletionException e) {
                                if(e.getCause() instanceof IOException || e.getCause() instanceof SpotifyWebApiException || e.getCause() instanceof ParseException) {
                                    System.out.println("Error while completing future: " + e.getCause().getMessage());
                                }
                                return null;
                            }
                        })
                        .filter(Objects::nonNull)
                        .flatMap(paging -> Arrays.stream(paging.getItems()))
                        .collect(Collectors.toList()));
    }

    @GetMapping("/user-token")
    public SpotifyUser getUserInfo() {
        return spotUser;
    }

    // @PostMapping("/refresh-token") 
    // public ResponseEntity<String> updateRefreshToken(@RequestBody String refreshToken) {
    //     this.refreshToken = refreshToken;
    //     return ResponseEntity.ok("Refresh token updated successfully");
    // }
    
    public String refreshAccessToken(String refreshToken) {
        try {

            AuthorizationCodeRefreshRequest refreshRequest = spotifyApi.authorizationCodeRefresh().refresh_token(refreshToken).build();
            AuthorizationCodeCredentials credentials = refreshRequest.execute();

            String newAccessToken = credentials.getAccessToken();
            spotifyApi.setAccessToken(newAccessToken);
            return newAccessToken;
        } catch(IOException | ParseException | SpotifyWebApiException e) {
            System.out.println("Error:" + e.getMessage());
            return null;
        }
    }

} 