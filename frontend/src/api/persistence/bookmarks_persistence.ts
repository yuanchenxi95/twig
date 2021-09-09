import {
    Bookmark,
    CreateBookmarkRequest,
    CreateBookmarkResponse,
    DeleteBookmarkResponse,
    ListBookmarkRequest,
    ListBookmarkResponse,
    UpdateBookmarkRequest,
    UpdateBookmarkResponse,
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

    list(
        listBookmarkRequest: ListBookmarkRequest,
    ): Observable<ListBookmarkResponse> {
        return this.ajaxClient
            .get<ListBookmarkResponse>(
                API_REQUEST_MAPPING.LIST_BOOKMARK(
                    listBookmarkRequest?.pageSize,
                    listBookmarkRequest?.pageToken,
                ),
            )
            .pipe(
                map((response) => ListBookmarkResponse.fromPartial(response)),
            );
    }

    delete(bookmarkId: string): Observable<DeleteBookmarkResponse> {
        return this.ajaxClient
            .delete<DeleteBookmarkResponse>(
                API_REQUEST_MAPPING.DELETE_BOOKMARK(bookmarkId),
            )
            .pipe(
                map((response) => DeleteBookmarkResponse.fromPartial(response)),
            );
    }

    update(
        bookmarkId: string,
        bookmark: Bookmark,
    ): Observable<UpdateBookmarkResponse> {
        const updateBookmarkRequest: UpdateBookmarkRequest = {
            bookmark,
            // eslint-disable-next-line @typescript-eslint/ban-ts-comment
            // @ts-ignore
            updateMask: 'tags,display_name',
        };
        return this.ajaxClient
            .put<UpdateBookmarkRequest, UpdateBookmarkResponse>(
                API_REQUEST_MAPPING.UPDATE_BOOKMARK(bookmarkId),
                updateBookmarkRequest,
            )
            .pipe(
                map((response) => UpdateBookmarkResponse.fromPartial(response)),
            );
    }
}
