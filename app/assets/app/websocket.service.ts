import { Injectable } from "@angular/core"
import { WebSocketSubject } from "rxjs/observable/dom/WebSocketSubject"
import { Subject } from "rxjs/Subject"

const WS_URL = "ws://localhost:9000/ws"

@Injectable()
export class WebSocketService {

    public messages: Subject<any>

    constructor() {
        this.messages = WebSocketSubject.create(WS_URL)
    }

    send(message: any) {
        this.messages.next(JSON.stringify(message))
    }

}