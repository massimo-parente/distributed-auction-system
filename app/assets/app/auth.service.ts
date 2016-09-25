import {Injectable} from "@angular/core"
import {Http, Headers} from "@angular/http"
import { Observable } from "rxjs/Observable"
import "rxjs/add/observable/of"
import "rxjs/add/operator/do"

@Injectable()
export class AuthService {

    private loggedIn = false
    private user = {name: "", role: ""}

    constructor(private http: Http) {}

    redirectUrl: string;

    login(username: string) {
        console.log("Logging in: " + username)
        let headers = new Headers()
        headers.append("Content-Type", "application/json")
        return this.http
            .post("/login", JSON.stringify({username}), {headers})
            .map((res:any) => {
                let js = res.json()
                this.user= {name: js.name, role: js.role}
                this.loggedIn = true
                res.ok
            })
    }

    logout() {
        console.log("Logging out")
        let headers = new Headers()
        headers.append("Content-Type", "application/json")
        return this.http
            .post("/logout", JSON.stringify(""), {headers})
            .map((res:any) => {
                localStorage.removeItem("user")
                this.loggedIn = false
                res.ok
            })
    }


    isLoggedIn() {
        return this.loggedIn
    }

    loggedUser() {
        return this.user
    }
}