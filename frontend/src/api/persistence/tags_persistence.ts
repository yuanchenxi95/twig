import { ListTagResponse } from 'proto/api/tag';
import { map, Observable } from 'rxjs';
import { AjaxClient } from '../ajax/ajax_client';
import { API_REQUEST_MAPPING } from './constants';

export class TagsPersistence {
    constructor(private readonly ajaxClient: AjaxClient) {}

    list(): Observable<ListTagResponse> {
        return this.ajaxClient
            .get<ListTagResponse>(API_REQUEST_MAPPING.LIST_TAGS)
            .pipe(map((response) => ListTagResponse.fromPartial(response)));
    }
}
