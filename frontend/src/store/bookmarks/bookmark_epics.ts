import { TwigApiError } from 'proto/api/twig_api_error';
import { Epic } from 'redux-observable';

import { catchError, filter, from, map, of, switchMap } from 'rxjs';
import { isActionOf } from 'typesafe-actions';
import { Dependencies } from '../dependencies';
import {
    BookmarkActionType,
    bookmarkCreateAsyncAction,
    bookmarkListAsyncAction,
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
                        bookmarkListAsyncAction.request(),
                    ),
                ),
            ),
        ),
    );

export const bookmarkEpics = [createBookmarkEpic];
