import {Injectable} from "@angular/core"
import {Http, Headers} from "@angular/http"
import "rxjs/add/operator/map"
import {BaseService} from "./base.service"
import RequestOptions = RequestOptions;

@Injectable()
export class PlayersService  extends BaseService {

    private _url = "http://localhost:9000/users/"

    constructor(private _http: Http) {
        super()
    }

    upload(payload: String) {
        let headers = new Headers({ 'Content-Type': 'text/plain' });
        return this._http.post("/load-players", payload, {headers: headers})
    }

    getPlayers() {
        return this._http.get("/players", this.getRequestOptions())
            .map(res => res.json())
    }

    getUsers(){
        return this._http.get(this._url, this.getRequestOptions())
            .map(res => res.json())
    }

    getUser(id){
        return this._http.get(this.getBaseUrl(id), this.getRequestOptions())
            .map(res => res.json())
    }

    addUser(user){
        return this._http.post(this._url, JSON.stringify(user), this.getRequestOptions())
            .map(res => res.json())
    }

    updateUser(user) {
        return this._http.put(this.getBaseUrl(user.id), JSON.stringify(user), this.getRequestOptions())
            .map(res => res.json())
    }

    deleteUser(id){
        return this._http.delete(this.getBaseUrl(id), this.getRequestOptions())
            .map(res => res.json())
    }

    private getBaseUrl(id) {
        return this._url + id
    }
}