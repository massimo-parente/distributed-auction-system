import {Injectable} from "@angular/core"
import {Http} from "@angular/http"
import "rxjs/add/operator/map"
import {BaseService} from "./base.service"

@Injectable()
export class TeamsService  extends BaseService {

    constructor(private _http: Http) {
        super()
    }

    getTeams() {
        return this._http.get("/teams", this.getRequestOptions())
            .map((res:any) => res.json())
    }

    getPlayers(team: string) {
        return this._http.get("/players/" + team, this.getRequestOptions())
            .map((res:any) => res.json())
    }


}