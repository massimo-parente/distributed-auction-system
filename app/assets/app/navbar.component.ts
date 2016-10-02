import {Component} from "@angular/core"
import {Router} from "@angular/router"
import {AuthService} from "./auth.service"

@Component({
    selector: "navbar",
    template: `
        <nav class="navbar navbar-inverse navbar-fixed-top">
            <div class="container">
                <div class="navbar-header">
                    <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#navbar" aria-expanded="false" aria-controls="navbar">
                        <span class="sr-only">Toggle navigation</span>
                        <span class="icon-bar"></span>
                        <span class="icon-bar"></span>
                        <span class="icon-bar"></span>
                    </button>
                <a class="navbar-brand" [routerLink]="['home']">Fantapesce Distributed Auction System</a>
            </div>
            <div id="navbar" class="navbar-collapse collapse" *ngIf="authService.isLoggedIn()">
              <ul class="nav navbar-nav">
                <li [routerLinkActive]="['active']"><a [routerLink]="['home']">Home</a></li>
                <li [routerLinkActive]="['active']"><a [routerLink]="['teams']">Teams</a></li>
                <li [routerLinkActive]="['active']"><a [routerLink]="['players']">Players</a></li>
                <li [routerLinkActive]="['active']"><a [routerLink]="['users']">Users</a></li>
              </ul>
              <ul class="nav navbar-nav navbar-right">
                  <li class="nav-item navbar-text" style="color: white">
                    <span class="glyphicon glyphicon-user" id="icon-user"></span> {{authService.loggedUser().name}}
                  </li>                        
                  <li>
                    <a class="clickable" (click)="logout()">
                        <span class="glyphicon glyphicon-log-out" id="icon-logout"></span> Log out
                    </a>
                </li>
              </ul>
            </div>
          </div>
        </nav>
    `
})
export class NavbarComponent {

    constructor(public authService: AuthService, private router: Router) {}

    logout() {
        this.authService.logout().subscribe(() => {
            this.router.navigate(["login"])
        })
    }

}

