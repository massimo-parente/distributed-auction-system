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
                <div *ngIf="isAwaitingInit()">
                    <h4><span class="text-info">Auction is currently closed</span></h4>
                </div>
                <div *ngIf="isAwaitingCall()">
                    <h4><span class="text-info">Awaiting call from {{ auctionService.getAuctioneer() }}</span></h4>
                </div>
                <div *ngIf="isAwaitingBidders()">                                        
                    <h4><span class="text-info">Awaiting bidders to join auction</span></h4>
                     <spinner></spinner>
                </div>
                <div class="form-group" *ngIf="isPendingJoinAuction()">
                    <h4><span class="text-info">Awaiting bidders to join auction</span></h4>
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
        return pendingBidders.indexOf(loggedUser) > -1
    }

    isAwaitingInit() {
        return this.auctionService.getAuctionStatus() == "auction-closed" ||
            this.auctionService.getAuctionStatus() == "auction-terminated"
    }

    isAwaitingCall() {
        return this.auctionService.getAuctionStatus() == "auction-initialised" &&
                this.auctionService.getAuctioneer() != this.authService.loggedUser().name
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