// import { createSelector } from 'reselect';

import { createSelector } from 'reselect';
import { LoadingState } from '../../common/loading_state';
import { BookmarkRootState, bookmarkState } from './bookmark_reducers';

export const selectBookmarkState = (state: BookmarkRootState) =>
    state.bookmark ?? bookmarkState;

export const selectBookmarkMapById = createSelector(
    selectBookmarkState,
    (state) => state.bookmarkMapById,
);

export const selectBookmarkList = createSelector(
    selectBookmarkMapById,
    (bookmarkMapById) => {
        return Array.from(bookmarkMapById.values());
    },
);

export const selectIsBookmarkLoading = createSelector(
    selectBookmarkState,
    (state) => state.loadingState === LoadingState.LOADING,
);

export const selectBookmarkLoadingState = createSelector(
    selectBookmarkState,
    (state) => state.loadingState,
);

export const selectBookmarkListNextPageToken = createSelector(
    selectBookmarkState,
    (state) => state.nextPageToken,
);
