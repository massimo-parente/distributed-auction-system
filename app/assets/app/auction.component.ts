import {Component} from "@angular/core"


@Component({
    selector: "auction",
    template: `
        <div class="container-fluid">
            <activity></activity>
            <chat></chat>
            <bid></bid>
            <admin></admin>
        </div>
    `
})
export class AuctionComponent {

    constructor() {
    }

}