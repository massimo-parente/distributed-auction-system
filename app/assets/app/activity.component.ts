import {Component, OnInit} from '@angular/core';
import {WebSocketService} from "./websocket.service";
import {AuctionService} from "./auction.service";

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
export class ActivityComponent {

    private messages = new Array<any>();

    constructor(private auctionService: AuctionService) {
        this.messages = auctionService.getMessages()
    }
}
