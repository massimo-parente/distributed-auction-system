import {Injectable} from "@angular/core"
import {Http, Headers, RequestOptions} from "@angular/http"
import "rxjs/add/observable/of"
import "rxjs/add/operator/do"

@Injectable()
export class EvenSourcingService {

    constructor(private http: Http) {
    }

    getEvent() {
        console.log("retrieving events")
        let headers = new Headers()
        headers.append("Content-Type", "application/json")
        return this.http.get("/events", new RequestOptions({headers})).map(res => res.json())
    }

}