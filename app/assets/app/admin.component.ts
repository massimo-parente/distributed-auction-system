import {Component} from '@angular/core';
import {AuctionService} from "./auction.service";
import {AuthService} from "./auth.service";

@Component({
    selector: 'admin',
    template: `
        <div class="panel panel-primary" *ngIf="showPanel()">
            <div class="panel-heading">Admin</div>
            <div class="panel-body">
                <div class="row">
                    <div class="col-md-4">                    
                        <button type="button" class="btn btn-primary" (click)="startAuction()">Start Auction</button>
                    </div>
                    <div class="col-md-4">                    
                        <button type="button" class="btn btn-primary" (click)="abortAuction()">Abort Auction</button>
                    </div>
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

    abortAuction() {
        this.auctionService.abortAuction().subscribe(() => {})
    }

    showPanel() {
        return this.authService.loggedUser().role == "admin"
    }
}