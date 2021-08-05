import { Tag } from 'proto/api/tag';
import { createReducer } from 'typesafe-actions';
import { LoadingState } from '../../common/loading_state';

import { TagActionType, tagListAsyncAction } from './tag_actions';

export interface TagState {
    tagMapById: ReadonlyMap<string, Tag>;
    loadingState: LoadingState;
}

export interface TagRootState {
    tag: TagState;
}

export const tagState: TagState = {
    tagMapById: new Map<string, Tag>(),
    loadingState: LoadingState.IDLE,
};

export const tagReducer = createReducer<TagState, TagActionType>(tagState)
    .handleAction(tagListAsyncAction.request, (state) => {
        return {
            ...state,
            loadingState: LoadingState.LOADING,
        };
    })
    .handleAction(tagListAsyncAction.success, (state, action) => {
        const listTagResponse = action.payload;
        const tagMapById = new Map(
            listTagResponse.tags.map((tag) => [tag.id, tag]),
        );
        return {
            ...state,
            tagMapById,
            loadingState: LoadingState.SUCCEEDED,
        };
    })
    .handleAction(tagListAsyncAction.failure, (state) => ({
        ...state,
        tagMapById: new Map(),
        loadingState: LoadingState.FAILED,
    }));
