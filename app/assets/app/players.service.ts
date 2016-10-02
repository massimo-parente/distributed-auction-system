import {Injectable} from "@angular/core"
import {Http, Headers} from "@angular/http"
import "rxjs/add/operator/map"
import {BaseService} from "./base.service"

@Injectable()
export class PlayersService  extends BaseService {

    constructor(private _http: Http) {
        super()
    }

    upload(payload: String) {
        let headers = new Headers({ 'Content-Type': 'text/plain' });
        return this._http.post("/load-players", payload, {headers: headers})
    }

    getPlayers() {
        return this._http.get("/players", this.getRequestOptions())
            .map((res:any) => res.json())
    }
}