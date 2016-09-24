import {Component} from '@angular/core';
import {AuctionService} from "./auction.service";
import {AuthService} from "./auth.service";

@Component({
    selector: 'admin',
    template: `
        <div class="panel panel-primary">
            <div class="panel-heading">Admin</div>
            <div class="panel-body">
                <div class="form-group">                    
                    <button type="button" class="btn btn-primary" (click)="startAuction()">Start Auction</button>
                </div>
            </div>
        </div>
    `
})
export class AdminComponent {

    constructor(private auctionService: AuctionService, private authService: AuthService) { }

    startAuction() {
        this.auctionService.startAuction().subscribe(() => {})
    }
 }