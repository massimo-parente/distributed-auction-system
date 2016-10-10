import { Injectable } from "@angular/core"
import { WebSocketSubject } from "rxjs/observable/dom/WebSocketSubject"
import { Subject } from "rxjs/Subject"

@Injectable()
export class WebSocketService {

    public messages: Subject<any>

    constructor() {
        let url = "ws://" + window.location.hostname + ":" + window.location.port + "/ws";
        console.log("Web-socket url: " + url)
        this.messages = WebSocketSubject.create(url)
    }

    send(message: any) {
        this.messages.next(JSON.stringify(message))
    }

}