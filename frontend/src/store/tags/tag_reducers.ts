import { Tag } from 'proto/api/tag';
import { createReducer } from 'typesafe-actions';
import { LoadingState } from '../../common/loading_state';
import { showErrorNotification } from '../../common/notification';

import {
    TagActionType,
    tagCreateAsyncAction,
    tagDeleteAsyncAction,
    tagListAsyncAction,
} from './tag_actions';

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
    .handleAction(
        [
            tagListAsyncAction.request,
            tagDeleteAsyncAction.request,
            tagCreateAsyncAction.request,
        ],
        (state) => {
            return {
                ...state,
                loadingState: LoadingState.LOADING,
            };
        },
    )
    .handleAction(
        tagListAsyncAction.success,
        (state, { payload: listTagResponse }) => {
            const tagMapById = new Map(
                listTagResponse.tags.map((tag) => [tag.id, tag]),
            );
            return {
                ...state,
                tagMapById,
                loadingState: LoadingState.SUCCEEDED,
            };
        },
    )
    .handleAction(tagDeleteAsyncAction.success, (state, { payload: tagId }) => {
        const tagMapById = new Map(state.tagMapById);
        tagMapById.delete(tagId);

        return {
            ...state,
            tagMapById,
            loadingState: LoadingState.SUCCEEDED,
        };
    })
    .handleAction(
        tagCreateAsyncAction.success,
        (state, { payload: createTagResponse }) => {
            const { tag } = createTagResponse;
            if (tag == null) {
                return state;
            }
            const tagMapById = new Map();
            tagMapById.set(tag.id, tag);
            for (const [key, value] of state.tagMapById.entries()) {
                tagMapById.set(key, value);
            }
            return {
                ...state,
                tagMapById,
                loadingState: LoadingState.SUCCEEDED,
            };
        },
    )
    .handleAction(
        [
            tagListAsyncAction.failure,
            tagDeleteAsyncAction.failure,
            tagCreateAsyncAction.failure,
        ],
        (state, action) => {
            showErrorNotification(action.payload.message);
            return {
                ...state,
                tagMapById: new Map(),
                loadingState: LoadingState.FAILED,
            };
        },
    );
