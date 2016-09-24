import {Component, OnInit} from "@angular/core";
import "rxjs/add/operator/switchMap"

import {TeamsService} from "./teams.service";


@Component({
    selector: 'teams',
    template: `
        <h1>Teams</h1>
        <div *ngFor="let team of teamDetails" class="panel panel-primary">
        <div class="panel-heading">
        <h3>{{team.name}}</h3>    
        </div>
        <div class="panel-body">
        <table class="table table-condensed">
            <thead>
                <tr>
                    <th>Name</th>
                    <th>Role</th>
                    <th>Value</th>
                </tr>
            </thead>
            <tbody>
                <tr *ngFor="let player of team.players">
                    <td>{{ player.name }}</td>
                    <td>{{ player.role }}</td>
                    <td>{{ player.value }}</td>
                </tr>
            </tbody>
        </table>
        </div>
        </div>
    `
})
export class TeamsComponent implements OnInit {

    teamDetails = new Array<any>()


    constructor(private teamsService: TeamsService) {
    }

    ngOnInit(): void {
        this.getTeams()
    }

    getTeams() {
        this.teamsService.getTeams()
            .subscribe((teams) => {
                teams.map((team) =>
                    this.getPlayers(team))
            })
    }

    getPlayers(team) {
        this.teamsService.getPlayers(team)
            .subscribe((players) =>
                this.teamDetails.push({
                    name: team,
                    players: players
                })
            )
    }
}