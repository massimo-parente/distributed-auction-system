import {Component, OnInit} from "@angular/core";
import {PlayersService} from "./players.service";
import {AuthService} from "./auth.service";

@Component({
    selector: 'players',
    template: `
        <div class="panel panel-primary" *ngIf="showUpload()">
            <div class="panel-heading">Upload players</div>
            <div class="panel-body">
                <div class="container-fluid">
                    <textarea [(ngModel)]="uploadCSV" style="width: 100%" 
                              placeholder="name, role [P,D,C,A], team"></textarea>
                </div>
                <div class="container-fluid">
                    <button class="btn btn-primary" (click)="upload()">Upload</button>
                </div>
            </div>
        </div>
        <h1>Players</h1>
        <table class="table table-condensed">
            <thead>
                <tr>
                    <th>Name</th>
                    <th>Role</th>
                    <th>Value</th>
                    <th>Team</th>
                    <th>Fanta Team</th>
                </tr>
            </thead>
            <tbody>
                <tr *ngFor="let player of players">
                    <td>{{ player.name }}</td>
                    <td>{{ player.role }}</td>
                    <td>{{ player.value }}</td>
                    <td>{{ player.team }}</td>
                    <td>{{ player.user }}</td>
                </tr>
            </tbody>
        </table>
    `
})
export class PlayersComponent implements OnInit {

    uploadCSV: string
    players: any

    constructor(private playersService: PlayersService, private authService: AuthService) {}

    ngOnInit() {
        this.playersService.getPlayers().subscribe(
            players => this.players = players
        )
    }

    upload() {
        this.playersService.upload(this.uploadCSV).subscribe(() => {})
    }

    showUpload() {
        return this.authService.isAdminLogged()

    }

}