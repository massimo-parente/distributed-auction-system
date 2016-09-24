import {Component, OnInit} from '@angular/core';
import {WebSocketService} from "./websocket.service";

@Component({
    selector: 'activity',
    template: `        
        <div class="panel panel-primary" style="height: 50%;">
          <div class="panel-heading">Activity</div>
          <div class="panel-body">      
                <p *ngFor="let msg of messages">{{ msg }}</p>
          </div>
        </div>
    `
})
export class ActivityComponent implements OnInit {

    private messages = new Array<any>();

    constructor(private webSocketService: WebSocketService) { }

    ngOnInit() {
        this.webSocketService.messages.subscribe((msg:any) => this.handle(msg));
    }

    handle(msg: any) {
        switch (msg.messageType) {
            case "chat": {
                this.push(msg.sender + " says: " + msg.message)
                break
            }
            case "bid-accepted": {
                this.push(msg.sender + " bids " + msg.value + " for " + msg.player)
                break
            }
            case "bid-rejected": {
                this.push(
                    msg.bid.sender + " bid of " +
                    msg.bid.value + " for " +
                    msg.bid.player + " has been rejected. Reason: " +
                    msg.message
                )
                break
            }
            case "subscribed": {
                this.push(msg.user + " has joined the auction")
                break
            }
            case "unsubscribed": {
                this.push(msg.user + " has left the auction")
                break
            }
            case "auction-opened": {
                this.push("All bidders have joined. Auction is on!!!")
                break
            }
            case "auction-requested": {
                this.push(msg.sender + " has requested an auction for " + msg.player)
                this.push("All bidders must join the auction")
                break
            }
            case "auction-joined": {
                this.push(msg.sender + " has joined the auction for " + msg.player)
                break
            }
            // case "auction-details": {
            //     if (msg.status == "pre-opening") {
            //         this.push("Next auctioneer is " + msg.auctioneer)
            //     }
            //     if (msg.status == "opened" && msg.count == 0) {
            //         this.push("Auction started!!!")
            //     }
            //     break
            // }
            case "auction-ticked":
                this.push(msg.sender + " e " + msg.count)
                break
            case "auction-completed":
                this.push(msg.sender + " has signed " + msg.player + " for " + msg.value)
                break
            default:
                // skip
        }
    }

    push(msg: string) {
        this.messages.push(msg);
        if (this.messages.length > 50) {
            this.messages.shift()
        }
    }
}
