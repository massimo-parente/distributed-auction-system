import { ModuleWithProviders }  from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { LoginComponent } from "./login.component";
import { AuctionComponent } from "./auction.component";
import { LoggedInGuard } from "./logged-in.guard";
import {TeamsComponent} from "./teams.component";
import {PlayersComponent} from "./players.component";
import {UsersComponent} from "./users.component";
import {UserDetailsComponent} from "./user-details.component";


export const appRoutes: Routes = [
    { path: "", redirectTo: "/home", pathMatch: "full" },
    { path: "home", component: AuctionComponent, canActivate: [LoggedInGuard] },
    { path: "login", component: LoginComponent},
    { path: "teams", component: TeamsComponent, canActivate: [LoggedInGuard]},
    { path: "players", component: PlayersComponent, canActivate: [LoggedInGuard]},
    { path: "users", component: UsersComponent, canActivate: [LoggedInGuard]},
    { path: "user-details", component: UserDetailsComponent, canActivate: [LoggedInGuard]},
    { path: "user-details/:id", component: UserDetailsComponent, canActivate: [LoggedInGuard]},
]

export const routing: ModuleWithProviders = RouterModule.forRoot(appRoutes);
