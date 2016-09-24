import {Injectable} from "@angular/core"
import {Http, Headers} from "@angular/http"
import { Observable } from "rxjs/Observable"
import "rxjs/add/observable/of"
import "rxjs/add/operator/do"

@Injectable()
export class AuthService {

    private loggedIn = false

    constructor(private http: Http) {
        this.loggedIn = !!localStorage.getItem("username")
    }

    // store the URL so we can redirect after logging in
    redirectUrl: string;

    login(username: string) {
        console.log("Logging in: " + username)
        let headers = new Headers()

        headers.append("Content-Type", "application/json")

        return this.http
            .post("http://localhost:9000/login", JSON.stringify({username}), {headers})
            .map((res:any) => {
                localStorage.setItem("username", username)
                this.loggedIn = true
                return res.ok
            })
    }

    logout() {
        console.log("Logging out")
        let headers = new Headers()

        headers.append("Content-Type", "application/json")

        return this.http
            .post("http://localhost:9000/logout", JSON.stringify(""), {headers})
            .map((res:any) => {
                localStorage.removeItem("username")
                this.loggedIn = false
                return res.ok
            })
    }


    isLoggedIn() {
        return this.loggedIn
    }

    loggedUser() {
        return localStorage.getItem("username")
    }
}