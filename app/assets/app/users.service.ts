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
            .map((res:any) => res.json())
    }

    getUser(id: any){
        return this._http.get("users/" + id, this.getRequestOptions())
            .map((res:any) => res.json())
    }

    addUser(user: any){
        return this._http.post("/users/add", JSON.stringify(user), this.getRequestOptions())
    }

    updateUser(user: any) {
        return this._http.put(
            "/users/update/" + user.name + "/" + user.role + "/" + user.budget,
            JSON.stringify(user),
            this.getRequestOptions()
        )
    }

    deleteUser(id: any){
        return this._http.delete("/users/delete/" + id, this.getRequestOptions())
    }
}