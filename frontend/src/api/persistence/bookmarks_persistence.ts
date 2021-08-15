import {
    CreateBookmarkRequest,
    CreateBookmarkResponse,
} from 'proto/api/bookmark';
import { map, Observable } from 'rxjs';
import { AjaxClient } from '../ajax/ajax_client';
import { API_REQUEST_MAPPING } from './constants';

export class BookmarksPersistence {
    constructor(private readonly ajaxClient: AjaxClient) {}

    create(
        createBookmarkRequest: CreateBookmarkRequest,
    ): Observable<CreateBookmarkResponse> {
        return this.ajaxClient
            .post<CreateBookmarkRequest, CreateBookmarkResponse>(
                API_REQUEST_MAPPING.CREATE_BOOKMARK,
                createBookmarkRequest,
            )
            .pipe(
                map((response) => CreateBookmarkResponse.fromPartial(response)),
            );
    }
}
