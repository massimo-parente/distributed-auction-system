import {Headers, RequestOptions} from "@angular/http"

export class BaseService {

    constructor(){
    }

    getRequestOptions() {
        let headers = new Headers({ 'Content-Type': 'application/json' });
        // let authToken = this._userService.getToken();
        // headers.append("Auth-Token", authToken);
        headers.append("username", localStorage.getItem("username"));
        let options = new RequestOptions({ headers: headers });
        return options;
    }
}

