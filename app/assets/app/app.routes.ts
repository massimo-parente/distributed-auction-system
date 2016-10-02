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
    { path: "teams", component: TeamsComponent},
    { path: "players", component: PlayersComponent},
    { path: "users", component: UsersComponent},
    { path: "user-details", component: UserDetailsComponent},
    { path: "user-details/:id", component: UserDetailsComponent},
]

export const routing: ModuleWithProviders = RouterModule.forRoot(appRoutes);
