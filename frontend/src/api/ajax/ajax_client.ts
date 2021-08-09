import { TwigApiError, TwigApiError_ErrorType } from 'proto/api/twig_api_error';
import { catchError, map, Observable, throwError } from 'rxjs';
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
        }).pipe(
            map((ajaxResponse) => ajaxResponse.response),
            catchError((error) => {
                if (error.response?.message != null) {
                    return throwError(() => error.response);
                } else {
                    const unknownError: TwigApiError = {
                        code: 400,
                        errorType: TwigApiError_ErrorType.UNKNOWN,
                        message: 'Twig server failed to respond with an error.',
                    };
                    return throwError(() => unknownError);
                }
            }),
        );
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
            httpMethod: HttpMethod.PUT,
            path,
            body,
        });
    }

    delete<R>(path: string): Observable<R> {
        return this.request({
            httpMethod: HttpMethod.DELETE,
            path,
        });
    }
}
