// import { createSelector } from 'reselect';

import { createSelector } from 'reselect';
import { LoadingState } from '../../common/loading_state';
import { TagRootState, tagState } from './tag_reducers';

export const selectTagState = (state: TagRootState) => state.tag ?? tagState;

export const selectTagMapById = createSelector(
    selectTagState,
    (state) => state.tagMapById,
);

export const selectTagList = createSelector(selectTagMapById, (tagMapById) => {
    return Array.from(tagMapById.values());
});

export const selectIsTagListLoading = createSelector(
    selectTagState,
    (state) => state.loadingState === LoadingState.LOADING,
);
