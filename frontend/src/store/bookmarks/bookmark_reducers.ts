import { Bookmark } from 'proto/api/bookmark';
import { createReducer } from 'typesafe-actions';
import { LoadingState } from '../../common/loading_state';
import { showErrorNotification } from '../../common/notification';

import {
    BookmarkActionType,
    bookmarkCreateAsyncAction,
    bookmarkDeleteAsyncAction,
    bookmarkListAsyncAction,
} from './bookmark_actions';

export interface BookmarkState {
    bookmarkMapById: ReadonlyMap<string, Bookmark>;
    loadingState: LoadingState;
}

export interface BookmarkRootState {
    bookmark: BookmarkState;
}

export const bookmarkState: BookmarkState = {
    bookmarkMapById: new Map<string, Bookmark>(),
    loadingState: LoadingState.IDLE,
};

export const bookmarkReducer = createReducer<BookmarkState, BookmarkActionType>(
    bookmarkState,
)
    .handleAction(
        [
            bookmarkListAsyncAction.request,
            bookmarkDeleteAsyncAction.request,
            bookmarkCreateAsyncAction.request,
        ],
        (state) => {
            return {
                ...state,
                loadingState: LoadingState.LOADING,
            };
        },
    )
    .handleAction(
        bookmarkListAsyncAction.success,
        (state, { payload: listBookmarkResponse }) => {
            const bookmarkMapById = new Map(
                listBookmarkResponse.bookmarks.map((bookmark) => [
                    bookmark.id,
                    bookmark,
                ]),
            );
            return {
                ...state,
                bookmarkMapById,
                loadingState: LoadingState.SUCCEEDED,
            };
        },
    )
    .handleAction(
        bookmarkDeleteAsyncAction.success,
        (state, { payload: bookmarkId }) => {
            const bookmarkMapById = new Map(state.bookmarkMapById);
            bookmarkMapById.delete(bookmarkId);

            return {
                ...state,
                bookmarkMapById,
                loadingState: LoadingState.SUCCEEDED,
            };
        },
    )
    .handleAction(
        bookmarkCreateAsyncAction.success,
        (state, { payload: createBookmarkResponse }) => {
            const { bookmark } = createBookmarkResponse;
            if (bookmark == null) {
                return state;
            }
            const bookmarkMapById = new Map();
            bookmarkMapById.set(bookmark.id, bookmark);
            for (const [key, value] of state.bookmarkMapById.entries()) {
                bookmarkMapById.set(key, value);
            }
            return {
                ...state,
                bookmarkMapById,
                loadingState: LoadingState.SUCCEEDED,
            };
        },
    )
    .handleAction(
        [
            bookmarkListAsyncAction.failure,
            bookmarkDeleteAsyncAction.failure,
            bookmarkCreateAsyncAction.failure,
        ],
        (state, action) => {
            showErrorNotification(action.payload.message);
            return {
                ...state,
                bookmarkMapById: new Map(),
                loadingState: LoadingState.FAILED,
            };
        },
    );
