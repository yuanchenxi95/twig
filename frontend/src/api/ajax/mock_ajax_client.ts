import { EMPTY, Observable } from 'rxjs';
import { AjaxClient, RequestParameter } from './ajax_client';

export class MockAjaxClient extends AjaxClient {
    constructor() {
        super('');
    }

    override request<T, R>(
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        requestParameter: RequestParameter<T>,
    ): Observable<R> {
        return EMPTY;
    }
}
