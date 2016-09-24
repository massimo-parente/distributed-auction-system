import {Component} from "@angular/core"
import {WebSocketService} from "./websocket.service"
import {PlayersService} from "./players.service";

@Component({
    selector: 'bid',
    template: `
        <div class="panel panel-primary">
            <div class="panel-heading">Auction</div>
            <div class="panel-body">
                <div *ngIf="showSpinner()" class="row">
                    <div class="col-md-12">
                        <spinner></spinner><span><h3>Waiting for bidders to join auction</h3></span>
                    </div>
                </div>
                <div class="form-group" *ngIf="validating()">
                    <button type="button" class="btn btn-primary" (click)="join()">Join Bid</button>
                </div>
                <div class="form-group" *ngIf="canRequestAuction()">
                    <select [(ngModel)]="player">
                        <option *ngFor="let player of players">{{player.name}}</option>
                    </select>
                    <input type="text" name="player" [(ngModel)]="player" placeholder="Enter player...">                    
                    <button type="button" class="btn btn-primary" (click)="startBid()">Call Bid</button>
                </div>
                <div class="form-group" *ngIf="canBid()">
                    <input type="text" name="player" [ngModel]="player" placeholder="Enter player..." disabled>
                    <input type="text" name="value" [(ngModel)]="value" placeholder="Enter bid...">
                    <button type="button" class="btn btn-primary" (click)="bid()">Bid</button>
                </div>
            </div>
        </div>
    `
})
export class BidComponent {

    players: any[]

    player: string = ""
    value: number = 0

    auctionDetails = {
        status: "",
        auctioneer: "",
        bid: {
            player: "",
            value: "",
            sender: ""
        },
        bidders: new Array<string>(),
        count: 0
    }

    constructor(
        private webSocketService: WebSocketService,
        private playersService: PlayersService
    ) {
        webSocketService.messages.subscribe((msg: any) => this.handle(msg));
        playersService.getPlayers().subscribe((players) => this.players = players)
    }

    handle(msg: any) {
        switch (msg.type) {
            case "auction-details":
                this.auctionDetails = msg
                this.player = msg.bid.player
                this.value = msg.bid.value
                break
            default:
            // skip
        }
    }

    canRequestAuction() {
        return this.auctionDetails.status == "pre-opening" &&
            this.auctionDetails.auctioneer == localStorage.getItem("username")
    }

    canBid() {
        return this.auctionDetails.status == "opened"
    }

    validating() {
        let joined = this.auctionDetails.bidders.indexOf(localStorage.getItem("username")) > -1
        return !joined && this.auctionDetails.status == "validation"
    }

    showSpinner() {
        let joined = this.auctionDetails.bidders.indexOf(localStorage.getItem("username")) > -1
        return joined && this.auctionDetails.status == "validation"
    }

    startBid() {
        let bid = {
            sender: localStorage.getItem("username"),
            player: this.player,
            value: 1,
            messageType: "request-auction"
        }
        this.webSocketService.send(bid)
    }

    bid() {
        let bid = {
            sender: localStorage.getItem("username"),
            player: this.auctionDetails.bid.player,
            value: this.value,
            messageType: "bid"
        }
        this.webSocketService.send(bid)
    }

    join() {
        let message = {
            sender: localStorage.getItem("username"),
            player: this.auctionDetails.bid.player,
            messageType: "join-auction"
        }
        this.webSocketService.send(message)
    }
}