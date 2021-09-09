import { TwigApiError } from 'proto/api/twig_api_error';
import { Epic } from 'redux-observable';

import { catchError, filter, from, map, of, switchMap } from 'rxjs';
import { isActionOf } from 'typesafe-actions';
import { Dependencies } from '../dependencies';
import {
    BookmarkActionType,
    bookmarkCreateAsyncAction,
    bookmarkDeleteAsyncAction,
    bookmarkListAsyncAction,
    bookmarkUpdateAsyncAction,
} from './bookmark_actions';
import { BookmarkRootState } from './bookmark_reducers';

export const createBookmarkEpic: Epic<
    BookmarkActionType,
    BookmarkActionType,
    BookmarkRootState,
    Dependencies
> = (action$, state$, { persistence }) =>
    action$.pipe(
        filter(isActionOf(bookmarkCreateAsyncAction.request)),
        switchMap(({ payload: createBookmarkRequest }) =>
            from(
                persistence.bookmarksPersistence.create(createBookmarkRequest),
            ).pipe(
                map((response) => bookmarkCreateAsyncAction.success(response)),
                catchError((error: TwigApiError) =>
                    of(
                        bookmarkCreateAsyncAction.failure(error),
                        bookmarkListAsyncAction.request({}),
                    ),
                ),
            ),
        ),
    );

export const listBookmarkEpic: Epic<
    BookmarkActionType,
    BookmarkActionType,
    BookmarkRootState,
    Dependencies
> = (action$, state$, { persistence }) =>
    action$.pipe(
        filter(isActionOf(bookmarkListAsyncAction.request)),
        switchMap(({ payload: listBookmarkRequest }) =>
            from(
                persistence.bookmarksPersistence.list(listBookmarkRequest),
            ).pipe(
                map((response) =>
                    bookmarkListAsyncAction.success({
                        nextPageToken: listBookmarkRequest.pageToken ?? null,
                        listBookmarkResponse: response,
                    }),
                ),
                catchError((error: TwigApiError) =>
                    of(bookmarkListAsyncAction.failure(error)),
                ),
            ),
        ),
    );

export const deleteBookmarkEpic: Epic<
    BookmarkActionType,
    BookmarkActionType,
    BookmarkRootState,
    Dependencies
> = (action$, state$, { persistence }) =>
    action$.pipe(
        filter(isActionOf(bookmarkDeleteAsyncAction.request)),
        switchMap(({ payload: bookmarkId }) =>
            from(persistence.bookmarksPersistence.delete(bookmarkId)).pipe(
                map(() => bookmarkDeleteAsyncAction.success(bookmarkId)),
                catchError((error: TwigApiError) =>
                    of(bookmarkDeleteAsyncAction.failure(error)),
                ),
            ),
        ),
    );

export const updateBookmarkEpic: Epic<
    BookmarkActionType,
    BookmarkActionType,
    BookmarkRootState,
    Dependencies
> = (action$, state$, { persistence }) =>
    action$.pipe(
        filter(isActionOf(bookmarkUpdateAsyncAction.request)),
        switchMap(({ payload: bookmark }) =>
            from(
                persistence.bookmarksPersistence.update(bookmark.id, bookmark),
            ).pipe(
                map((updateBookmarkResponse) =>
                    bookmarkUpdateAsyncAction.success(updateBookmarkResponse),
                ),
                catchError((error: TwigApiError) =>
                    of(bookmarkDeleteAsyncAction.failure(error)),
                ),
            ),
        ),
    );

export const bookmarkEpics = [
    createBookmarkEpic,
    listBookmarkEpic,
    deleteBookmarkEpic,
    updateBookmarkEpic,
];
