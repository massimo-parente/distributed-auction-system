import {Component} from "@angular/core"
import {UsersService} from "./users.service";
import {AuthService} from "./auth.service";

@Component({
    selector: "users",
    template: `
        <h1>Users</h1>
        <div class="form-group" *ngIf="authService.isAdminLogged()">
            <a role="button" class="btn btn-primary" [routerLink]="['/user-details']">Add user</a>
        </div>
        <table class="table table-condensed">
            <thead>
                <tr>
                    <th>Name</th>
                    <th>Role</th>
                    <th>Budget</th>
                    <th *ngIf="authService.isAdminLogged()">Edit</th>
                    <th *ngIf="authService.isAdminLogged()">Delete</th>
                </tr>
            </thead>
            <tbody>
                <tr *ngFor="let user of users">
                    <td>{{ user.name }}</td>
                    <td>{{ user.role }}</td>
                    <td>{{ user.budget }}</td>
                    <td *ngIf="authService.isAdminLogged()">                        
                        <i class="glyphicon glyphicon-edit clickable" [routerLink]="['/user-details/', user.name]"></i>                        
                    </td>
                    <td *ngIf="authService.isAdminLogged()">
                        <i class="glyphicon glyphicon-remove clickable" (click)="delete(user)"></i>
                    </td>
                </tr>
            </tbody>
        </table>
    `
})
export class UsersComponent {

    users: any[]

    constructor(private usersService: UsersService, public authService: AuthService) {
    }

    ngOnInit() {
        this.usersService.getUsers()
            .subscribe(
                users => {
                    this.users = users
                },
                err => {
                    console.log("Error " + err); // improve
                }
            )
    }

    edit(user) {
        console.log("editing" + user.name)
    }

    delete(user) {
        console.log("deleting" + user.name)
    }
}