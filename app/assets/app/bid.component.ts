import {Component, OnInit} from "@angular/core"
import {WebSocketService} from "./websocket.service"
import {PlayersService} from "./players.service";
import {AuctionService} from "./auction.service";
import {AuthService} from "./auth.service";

@Component({
    selector: 'bid',
    template: `
        <div class="panel panel-primary">
            <div class="panel-heading">Auction</div>
            <div class="panel-body">
                <div *ngIf="isAwaitingBidders()" class="row">
                    <div class="col-md-12">
                        <spinner></spinner><span><h3>Waiting for bidders to join auction</h3></span>
                    </div>
                </div>
                <div class="form-group" *ngIf="isPendingJoinAuction()">
                    <button type="button" class="btn btn-primary" (click)="joinAuction()">Join Bid</button>
                </div>
                <div class="form-group" *ngIf="canRequestAuction()">
                    <select [(ngModel)]="player">
                        <option *ngFor="let player of players">{{player.name}}</option>
                    </select>
                    <input type="text" name="player" [(ngModel)]="player" placeholder="Enter player...">                    
                    <button type="button" class="btn btn-primary" (click)="requestAuction()">Call Bid</button>
                </div>
                <div class="form-group" *ngIf="canBid()">
                    <input type="text" 
                            name="player" 
                            [ngModel]="auctionService.getHighestBid().player" 
                            placeholder="Enter player..." 
                            disabled>
                    <input type="text" name="value" [(ngModel)]="value" placeholder="Enter bid...">
                    <button type="button" class="btn btn-primary" (click)="bid()">Bid</button>
                </div>
            </div>
        </div>
    `
})
export class BidComponent implements OnInit {

    players: any[]
    player: string = ""
    value: number = 0

    constructor(
        private webSocketService: WebSocketService,
        private playersService: PlayersService,
        public auctionService: AuctionService,
        private authService: AuthService
    ) { }

    ngOnInit() {
        console.log("fetching players")
        this.playersService.getPlayers()
            .subscribe((players) => this.players = players)
    }

    canRequestAuction() {
        return this.auctionService.getAuctioneer() == this.authService.loggedUser().name &&
            this.auctionService.getAuctionStatus() == "auction-initialised"
    }

    userJoined() {
        let pendingBidders = this.auctionService.getPendingBidders()
        let loggedUser = this.authService.loggedUser().name
        console.log("aa " + loggedUser)
        return pendingBidders.indexOf(loggedUser) > -1
    }

    isPendingJoinAuction() {
        return this.userJoined() && this.auctionService.getAuctionStatus() == "auction-requested"
    }

    isAwaitingBidders() {
        return !this.userJoined() && this.auctionService.getAuctionStatus() == "auction-requested"
    }

    canBid() {
        return this.auctionService.getAuctionStatus() == "auction-opened"
    }

    requestAuction() {
        let bid = {
            auctioneer: this.authService.loggedUser().name,
            player: this.player,
            messageType: "call-auction"
        }
        this.webSocketService.send(bid)
    }

    joinAuction() {
        let message = {
            bidder: this.authService.loggedUser().name,
            player: this.auctionService.getHighestBid().player,
            messageType: "join-auction"
        }
        this.webSocketService.send(message)
    }

    bid() {
        let bid = {
            bidder: this.authService.loggedUser().name,
            player: this.auctionService.getHighestBid().player,
            value: this.value,
            messageType: "bid"
        }
        this.webSocketService.send(bid)
    }
}