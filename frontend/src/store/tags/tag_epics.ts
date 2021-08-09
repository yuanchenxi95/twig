import { TwigApiError } from 'proto/api/twig_api_error';
import { Epic } from 'redux-observable';

import { catchError, filter, from, map, of, switchMap } from 'rxjs';
import { isActionOf } from 'typesafe-actions';
import { Dependencies } from '../dependencies';
import {
    TagActionType,
    tagCreateAsyncAction,
    tagDeleteAsyncAction,
    tagListAsyncAction,
} from './tag_actions';
import { TagRootState } from './tag_reducers';

export const listTagEpic: Epic<
    TagActionType,
    TagActionType,
    TagRootState,
    Dependencies
> = (action$, state$, { persistence }) =>
    action$.pipe(
        filter(isActionOf(tagListAsyncAction.request)),
        switchMap(() =>
            from(persistence.tagsPersistence.list()).pipe(
                map((response) => tagListAsyncAction.success(response)),
                catchError((error: TwigApiError) =>
                    of(tagListAsyncAction.failure(error)),
                ),
            ),
        ),
    );

export const deleteTagEpic: Epic<
    TagActionType,
    TagActionType,
    TagRootState,
    Dependencies
> = (action$, state$, { persistence }) =>
    action$.pipe(
        filter(isActionOf(tagDeleteAsyncAction.request)),
        switchMap(({ payload: tagId }) =>
            from(persistence.tagsPersistence.delete(tagId)).pipe(
                map(() => tagDeleteAsyncAction.success(tagId)),
                catchError((error: TwigApiError) =>
                    of(
                        tagDeleteAsyncAction.failure(error),
                        tagListAsyncAction.request(),
                    ),
                ),
            ),
        ),
    );

export const createTagEpic: Epic<
    TagActionType,
    TagActionType,
    TagRootState,
    Dependencies
> = (action$, state$, { persistence }) =>
    action$.pipe(
        filter(isActionOf(tagCreateAsyncAction.request)),
        switchMap(({ payload: createTagRequest }) =>
            from(persistence.tagsPersistence.create(createTagRequest)).pipe(
                map((response) => tagCreateAsyncAction.success(response)),
                catchError((error: TwigApiError) =>
                    of(
                        tagCreateAsyncAction.failure(error),
                        tagListAsyncAction.request(),
                    ),
                ),
            ),
        ),
    );

export const tagEpics = [listTagEpic, deleteTagEpic, createTagEpic];
