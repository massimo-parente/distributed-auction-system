import {Component} from "@angular/core";
import {PlayersService} from "./players.service";

@Component({
    selector: 'players',
    template: `
        <div class="panel panel-primary">
            <div class="panel-heading">Upload players</div>
            <div class="panel-body">
                <textarea [(ngModel)]="players"></textarea>
                <button class="btn btn-primary" (click)="upload()">Upload</button>
            </div>
        </div>
    `
})
export class PlayersComponent {

    players: string

    constructor(private playersService: PlayersService) {
    }

    upload() {
        this.playersService.upload(this.players).subscribe(() => {})
    }

    //onChange file listener
    changeListener($event): void {
        //this.files = $event.target
    }
}