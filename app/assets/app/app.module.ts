import {NgModule}      from "@angular/core"
import {BrowserModule} from "@angular/platform-browser"
import {HttpModule, JsonpModule} from "@angular/http"
import {FormsModule} from "@angular/forms"

import {routing} from "./app.routes"

import {AppComponent}  from "./app.component"
import {NavbarComponent} from "./navbar.component"
import {ActivityComponent} from "./activity.component"
import {ChatComponent} from "./chat.component"
import {BidComponent} from "./bid.component"
import {WebSocketService} from "./websocket.service"
import {AuthService} from "./auth.service"
import {AuctionComponent} from "./auction.component"
import {LoggedInGuard} from "./logged-in.guard"
import {UsersComponent} from "./users.component"
import {UsersService} from "./users.service"
import {BaseService} from "./base.service"
import {LoginComponent} from "./login.component"
import {PlayersComponent} from "./players.component"
import {SpinnerComponent} from "./spinner.component"
import {AuctionService} from "./auction.service"
import {AdminComponent} from "./admin.component"
import {TeamsService} from "./teams.service"
import {TeamsComponent} from "./teams.component"
import {PlayersService} from "./players.service"
import {UserDetailsComponent} from "./user-details.component"
import {EvenSourcingService} from "./event-sourcing.service"

@NgModule({
    imports: [BrowserModule, FormsModule, HttpModule, JsonpModule, routing],
    declarations: [
        AppComponent, NavbarComponent, ActivityComponent, ChatComponent, BidComponent, AuctionComponent, UsersComponent,
        LoginComponent, PlayersComponent, SpinnerComponent, AppComponent, AdminComponent, TeamsComponent, UserDetailsComponent
    ],
    providers: [
        WebSocketService, AuthService, LoggedInGuard, UsersService, BaseService, AuctionService, TeamsService,
        PlayersService, EvenSourcingService],
    bootstrap: [AppComponent]
})
export class AppModule {
}