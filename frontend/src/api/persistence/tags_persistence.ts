import {
    CreateTagRequest,
    CreateTagResponse,
    DeleteTagResponse,
    ListTagResponse,
} from 'proto/api/tag';
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

    delete(tagId: string): Observable<DeleteTagResponse> {
        return this.ajaxClient
            .delete<DeleteTagResponse>(API_REQUEST_MAPPING.DELETE_TAG(tagId))
            .pipe(map((response) => DeleteTagResponse.fromPartial(response)));
    }

    create(createTagRequest: CreateTagRequest): Observable<CreateTagResponse> {
        return this.ajaxClient
            .post<CreateTagRequest, CreateTagResponse>(
                API_REQUEST_MAPPING.CREATE_TAG,
                createTagRequest,
            )
            .pipe(map((response) => CreateTagResponse.fromPartial(response)));
    }
}
