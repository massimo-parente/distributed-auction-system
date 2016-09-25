import {Injectable} from "@angular/core"
import {Http, Headers, RequestOptions} from "@angular/http"
import "rxjs/add/observable/of"
import "rxjs/add/operator/do"
import {WebSocketService} from "./websocket.service"

@Injectable()
export class AuctionService {

    private auctionStatus = ""
    private auctioneer = ""
    private highestBid = {bidder: "", player: "", value: 0}
    private pendingBidders = new Array<string>()
    private messages = new Array<any>();

    constructor(private webSocketService: WebSocketService, private http: Http) {
        this.webSocketService.messages
            .subscribe((msg: any) => this.handle(msg));
    }

    handle(msg: any) {
        switch(msg.messageType) {
            case "subscribed":
                this.pushMessage(msg.user + " has joined the auction")
                break
            case "unsubscribed":
                this.pushMessage(msg.user + " has left the auction")
                break
            case "chat":
                this.pushMessage(msg.sender + " says: " + msg.message)
                break
            case "auction-initialised":
                this.pushMessage("Next auctioneer is " + msg.auctioneer)
                this.auctionStatus = msg.messageType
                this.auctioneer = msg.auctioneer
                break
            case "auction-requested":
                this.pushMessage(msg.auctioneer + " has requested an auction for " + msg.player)
                this.pushMessage("All bidders must join the auction")
                this.auctionStatus = msg.messageType
                this.highestBid = {bidder: msg.auctioneer, player: msg.player, value: 1}
                this.pendingBidders = msg.pendingBidders
                break
            case "auction-joined":
                this.pushMessage(msg.sender + " has joined the auction for " + msg.player)
                break
            case "auction-opened":
                this.pushMessage("All bidders have joined. Auction is on!!!")
                this.auctionStatus = msg.messageType
                break
            case "bid-accepted":
                this.pushMessage(msg.bid.bidder + " bids " + msg.bid.value + " for " + msg.bid.player)
                break
            case "bid-rejected":
                this.pushMessage(
                    msg.bid.bidder + " bid of " +
                    msg.bid.value + " for " +
                    msg.bid.player + " has been rejected. " +
                    msg.reason
                )
                break
            case "auction-ticked":
                this.pushMessage(msg.bid.bidder + " for " + msg.bid.player + " " + msg.count)
                break
            case "auction-completed":
                this.pushMessage(msg.bid.bidder + " has signed " + msg.bid.player + " for " + msg.bid.value)
                break
            default:
                // skip
        }
    }

    startAuction() {
        console.log("starting auction")
        let headers = new Headers()
        headers.append("Content-Type", "application/json")
        return this.http.post("/start-auction", null, new RequestOptions({headers}))
    }

    abortAuction() {
        console.log("aborting auction")
        let headers = new Headers()
        headers.append("Content-Type", "application/json")
        return this.http.post("/abort-auction", null, new RequestOptions({headers}))
    }

    getAuctionStatus() {
        return this.auctionStatus
    }

    getAuctioneer() {
        return this.auctioneer
    }

    getPendingBidders() {
        return this.pendingBidders
    }

    getHighestBid() {
        return this.highestBid
    }

    getMessages() {
        return this.messages
    }

    pushMessage(msg: string) {
        this.messages.push(msg);
        if (this.messages.length > 20) {
            this.messages.shift()
        }
    }

}