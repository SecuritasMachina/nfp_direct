import {Injectable} from '@angular/core';
import {Observable, throwError} from "rxjs";
import {retry, catchError} from 'rxjs/operators';

import {ApiResponse} from "../model/api.response";
import {HttpClient} from "@angular/common/http";
import {isDevMode} from '@angular/core';

@Injectable({
    providedIn: 'root'
})
export class RestService {
    baseURL = '';

    constructor(private http: HttpClient) {
        if (isDevMode()) {
            this.baseURL = '//localhost:8080/rest';
        } else {
            this.baseURL = 'rest';
        }
    }

    getUrls(): Observable<any> {
        return this.http.get<ApiResponse>(this.baseURL + '/showByOwner');
    }

    getDeletedUrls(): Observable<any> {
        return this.http.get<ApiResponse>(this.baseURL + '/showDeletedByOwner');
    }

    checkAlreadyExists(pUrl) {
        return this.http.get(this.baseURL + '/checkAlreadyExists/' + pUrl.shortName);
    }

    deleteUrl(pUrl) {
        return this.http.delete(this.baseURL + '/delete/' + pUrl.id);
    }

    restoreUrl(pUrl) {
        console.log('restoreurl');
        return this.http.get(this.baseURL + '/restore/' + pUrl.id);
    }

    addUrl(pUrl) {
        return this.http.post(this.baseURL + '/save', pUrl);
    }
}
