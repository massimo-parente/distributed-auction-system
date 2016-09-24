import {Component} from "@angular/core"
import {Router} from "@angular/router"
import {AuthService} from "./auth.service"

@Component({
    selector: "navbar",
    template: `
        <nav class="navbar navbar-default">
          <div class="container-fluid">
            <div class="navbar-header">
              <button type="button" class="navbar-toggle" data-toggle="collapse" data-target="#myNavbar">
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
              </button>
              <a class="navbar-brand" [routerLink]="['home']">Fantapesce Distributed Auction System</a>
            </div>
            <div class="collapse navbar-collapse" id="myNavbar">
              <ul class="nav navbar-nav">
                <li class="active"><a [routerLink]="['home']">Home</a></li>
                <li class="active"><a [routerLink]="['teams']">Teams</a></li>
                <li class="active"><a [routerLink]="['players']">Players</a></li>
                <li class="active"><a [routerLink]="['users']">Users</a></li>
              </ul>
              <ul class="nav navbar-nav navbar-right" *ngIf="authService.isLoggedIn()">
                  <li class="navbar-text" style="color: white">
                    <span class="glyphicon glyphicon-user" id="icon-user"></span> {{authService.loggedUser()}}
                  </li>                        
                  <li>
                    <a (click)="logout()">
                        <span class="glyphicon glyphicon-log-out" id="icon-logout"></span> Log out
                    </a>
                </li>
              </ul>
            </div>
          </div>
        </nav>
  <!---->
        <!--<nav class="navbar navbar-default">-->
            <!--<div class="container-fluid">-->
                <!--&lt;!&ndash; Brand and toggle get grouped for better mobile display &ndash;&gt;-->
                <!--<div class="navbar-header">-->
                    <!--<a class="navbar-brand" (click)="teams()">Fantapesce Distributed Auction System</a>-->
                <!--</div>-->
                <!--<ul class="nav navbar-nav">-->
                    <!--<li class="navbar-text">-->
                        <!--<a href="#">Home</a>-->
                    <!--</li>-->
                    <!--<li class="navbar-text">-->
                        <!--<a href="#">Teams</a>-->
                    <!--</li>-->
                <!--</ul>    -->
                <!--<ul class="nav navbar-nav navbar-right" *ngIf="authService.isLoggedIn()">-->
                    <!--<li class="navbar-text" style="color: white">-->
                        <!--<span class="glyphicon glyphicon-user" id="icon-user"></span> {{authService.loggedUser()}}-->
                    <!--</li>                        -->
                    <!--<li>-->
                        <!--<a (click)="logout()">-->
                            <!--<span class="glyphicon glyphicon-log-out" id="icon-logout"></span> Log out-->
                        <!--</a>-->
                    <!--</li>-->
                <!--</ul>                          -->
                <!--&lt;!&ndash; Collect the nav links, forms, and other content for toggling &ndash;&gt;-->
                <!--<div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">-->
                    <!--<ul class="nav navbar-nav">-->
                        <!--<li class="navbar-text">-->
                            <!--<a href="#">Teams</a>-->
                        <!--</li>-->
                    <!--</ul>-->
                    <!--<ul class="nav navbar-nav navbar-right" *ngIf="authService.isLoggedIn()">-->
                        <!--<li class="navbar-text" style="color: white">-->
                            <!--<span class="glyphicon glyphicon-user" id="icon-user"></span> {{authService.loggedUser()}}-->
                        <!--</li>                        -->
                        <!--<li>-->
                            <!--<a (click)="logout()">-->
                                <!--<span class="glyphicon glyphicon-log-out" id="icon-logout"></span> Log out-->
                            <!--</a>-->
                        <!--</li>-->
                    <!--</ul>-->
                <!--</div>&lt;!&ndash; /.navbar-collapse &ndash;&gt;-->
            <!--</div>&lt;!&ndash; /.container-fluid &ndash;&gt;-->
        <!--</nav>-->
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

