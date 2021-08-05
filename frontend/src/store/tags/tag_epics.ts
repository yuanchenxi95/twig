import { TwigApiError } from 'proto/api/twig_api_error';
import { Epic } from 'redux-observable';

import { catchError, filter, from, map, of, switchMap } from 'rxjs';
import { isActionOf } from 'typesafe-actions';
import { Dependencies } from '../dependencies';
import { TagActionType, tagListAsyncAction } from './tag_actions';
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
                map(tagListAsyncAction.success),
                catchError((error: TwigApiError) =>
                    of(tagListAsyncAction.failure(error)),
                ),
            ),
        ),
    );

export const tagEpics = [listTagEpic];
