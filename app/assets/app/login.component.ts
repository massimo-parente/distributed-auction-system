import {Component} from "@angular/core"
import {Router} from "@angular/router"
import {AuthService} from "./auth.service";

@Component({
    selector: "login",
    template: `
        <div class="container">
            <form (ngSubmit)="login()">
                <h2 class="form-signin-heading">Please sign in</h2>
                <label for="username" class="sr-only">Username</label>
                <input type="text" name="username" [(ngModel)]="username" class="form-control" placeholder="nome..." required autofocus>
                <button class="btn btn-lg btn-primary btn-block" type="submit">Sign in</button>
                <div class="alert alert-danger" *ngIf="errorMessage">{{errorMessage}}</div>
            </form>
        </div>        
    `
})
export class LoginComponent {

    username: string
    errorMessage: any

    constructor(private authService: AuthService, private router: Router) {
    }

    login() {
        this.authService.login(this.username).subscribe(
            user => {
                this.router.navigate(['home'])
            },
            err => {
                this.errorMessage = <any>err
            }
        );
    }
}