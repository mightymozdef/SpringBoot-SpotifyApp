import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { TopArtistsComponent } from "./components/top-artists/top-artists.component";
import { LoginComponent } from "./components/login/login.component";
import { UserComponent } from './components/user/user.component';
import { PlaylistsComponent } from './components/playlists/playlists.component';

const routes: Routes = [
  { path: '', component: LoginComponent }, //default route is ''
  { path: 'top-artists', component: TopArtistsComponent },
  { path: 'playlists', component: PlaylistsComponent },
  { path: 'user', component: UserComponent }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
