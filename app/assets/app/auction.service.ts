import {Injectable} from "@angular/core"
import {Http, Headers, RequestOptions} from "@angular/http"
import "rxjs/add/observable/of"
import "rxjs/add/operator/do"
import {WebSocketService} from "./websocket.service"

@Injectable()
export class AuctionService {

    auctionStatus = ""

    constructor(private webSocketService: WebSocketService, private http: Http) {
        this.webSocketService.messages.subscribe((msg: any) => this.handle(msg));
    }

    handle(msg: any) {
        this.auctionStatus = msg.messageType
    }

    startAuction() {
        console.log("starting auction")
        let headers = new Headers()
        headers.append("Content-Type", "application/json")
        return this.http.post("http://localhost:9000/start-auction", null, new RequestOptions({headers}))
    }

    getAuctionStatus() {
        return this.auctionStatus
    }

}