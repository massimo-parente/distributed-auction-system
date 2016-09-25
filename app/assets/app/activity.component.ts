import {Component, OnInit,  ElementRef, ViewChild, AfterViewChecked} from '@angular/core';
import {AuctionService} from "./auction.service";

@Component({
    selector: 'activity',
    template: `      
        <div class="panel panel-primary" >
          <div class="panel-heading">Activity</div>
          <div #scrollablePanel class="panel-body" style="min-height: 300px;max-height: 300px; overflow-y: scroll">      
                <p *ngFor="let msg of messages">{{ msg }}</p>
          </div>
        </div>
    `
})
export class ActivityComponent implements OnInit, AfterViewChecked {
    @ViewChild("scrollablePanel") private scrollablePanel: ElementRef;

    private messages = new Array<any>();

    constructor(private auctionService: AuctionService) {}

    ngOnInit() {
        this.messages = this.auctionService.getMessages()
        this.scrollToBottom()
    }

    ngAfterViewChecked() {
        this.scrollToBottom()
    }

    scrollToBottom(): void {
        this.scrollablePanel.nativeElement.scrollTop = this.scrollablePanel.nativeElement.scrollHeight;
    }
}
