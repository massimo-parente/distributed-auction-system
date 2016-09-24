import {Component, OnInit} from "@angular/core"
import {Router, ActivatedRoute} from "@angular/router"
import {UsersService} from "./users.service";

@Component({
    selector: "users",
    template: `
        <h1>{{ title }}</h1>
        <div class="form-group">
            <label for="name">Name</label>
            <input type="text" name="name" [(ngModel)]="name" placeholder="Name...">
        </div>
        <div class="form-group">
            <label for="role">Role</label>
            <input type="text" name="role" [(ngModel)]="role" placeholder="Role...">
        </div>
        <div class="form-group">
            <label for="budget">Budget</label>
            <input type="number" name="budget" [(ngModel)]="budget" placeholder="1">
        </div>
        <div>
            <button *ngIf="newUser" name="save" class="btn btn-primary" (click)="save()">Save</button>
            <button *ngIf="!newUser" name="update" class="btn btn-primary" (click)="update()">Save</button>
        </div>
    `
})
export class UserDetailsComponent implements OnInit {

    title = ""

    name = ""
    role = ""
    budget = 0

    newUser = false

    constructor(private usersService: UsersService, private route: ActivatedRoute) {
    }

    ngOnInit() {
        let name = this.route.snapshot.params["id"]
        if (!name) {
            this.title = "Add user"
            this.newUser = true
            return
        }
        this.usersService.getUser(name)
            .subscribe((user) => {
                this.name = user.name
                this.role = user.role
                this.budget = user.budget
                this.title = "Edit user " + name
            })
    }

    save() {
        let user = {
            name: this.name,
            role: this.role,
            budget: this.budget
        }
        console.log("adding user " + JSON.stringify(user))
        this.usersService.addUser(user).subscribe(() => {})
    }

    update() {
        let user = {
            name: this.name,
            role: this.role,
            budget: this.budget
        }
        console.log("updating user " + JSON.stringify(user))
        this.usersService.updateUser(user).subscribe(() => {})
    }

}