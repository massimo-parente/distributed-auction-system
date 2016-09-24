import {Component} from '@angular/core';
import {WebSocketService} from "./websocket.service";

@Component({
    selector: 'chat',
    template: `
        <div class="panel panel-primary">
            <div class="panel-heading">Chat</div>
            <div class="panel-body">
                <div class="form-group">
                    <input type="text" [(ngModel)]="chatMessage" name="message" placeholder="Enter message...">
                    <button type="button" class="btn btn-primary" (click)="sendMessage()">Send</button>
                </div>
            </div>
        </div>
    `
})
export class ChatComponent {

    chatMessage: string;

    constructor(private webSocketService: WebSocketService) { }

    sendMessage() {

        console.log("sending message")
        let message = {
            messageType: "chat",
            sender: localStorage.getItem("username"),
            message: this.chatMessage
        }

        this.webSocketService.send(message)
        this.chatMessage = '';
    }
 }