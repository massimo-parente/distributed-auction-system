import {Injectable} from "@angular/core"
import {Http} from "@angular/http"
import "rxjs/add/operator/map"
import {BaseService} from "./base.service"

@Injectable()
export class UsersService  extends BaseService {

    constructor(private _http: Http) {
        super()
    }

    getUsers(){
        return this._http.get("/users", this.getRequestOptions())
            .map(res => res.json())
    }

    getUser(id){
        return this._http.get("users/" + id, this.getRequestOptions())
            .map(res => res.json())
    }

    addUser(user){
        return this._http.post("/users/add", JSON.stringify(user), this.getRequestOptions())
    }

    updateUser(user) {
        return this._http.put(
            "/users/update/" + user.name + "/" + user.role + "/" + user.budget,
            JSON.stringify(user),
            this.getRequestOptions()
        )
    }

    deleteUser(id){
        return this._http.delete("/users/delete/" + id, this.getRequestOptions())
    }
}