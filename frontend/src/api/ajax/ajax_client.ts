import { map, Observable } from 'rxjs';
import { ajax } from 'rxjs/ajax';

export enum HttpMethod {
    GET = 'GET',
    POST = 'POST',
    PUT = 'PUT',
    DELETE = 'DELETE',
}

export interface RequestParameter<T> {
    httpMethod: HttpMethod;
    path: string;
    body?: T;
}

export class AjaxClient {
    constructor(private readonly baseUrl: string) {}

    request<T, R>({
        httpMethod,
        path,
        body,
    }: RequestParameter<T>): Observable<R> {
        return ajax<R>({
            url: this.baseUrl + encodeURI(path),
            method: httpMethod,
            headers: {
                'Content-Type': 'application/json',
            },
            body,
        }).pipe(map((ajaxResponse) => ajaxResponse.response));
    }

    get<R>(path: string): Observable<R> {
        return this.request({
            httpMethod: HttpMethod.GET,
            path,
        });
    }

    post<T, R>(path: string, body: T): Observable<R> {
        return this.request({
            httpMethod: HttpMethod.POST,
            path,
            body,
        });
    }

    put<T, R>(path: string, body: T): Observable<R> {
        return this.request({
            httpMethod: HttpMethod.POST,
            path,
            body,
        });
    }

    delete<R>(path: string): Observable<R> {
        return this.request({
            httpMethod: HttpMethod.POST,
            path,
        });
    }
}
