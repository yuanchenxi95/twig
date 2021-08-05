import { TwigApiError } from 'proto/api/twig_api_error';
import { Epic } from 'redux-observable';
import { catchError, filter, from, map, of, switchMap } from 'rxjs';
import { isActionOf } from 'typesafe-actions';
import { Dependencies } from '../dependencies';
import {
    AuthenticationActionType,
    authenticationGetAsyncAction,
} from './authentication_actions';
import { AuthenticationRootState } from './authentication_reducers';

export const loadUserInformationEpic: Epic<
    AuthenticationActionType,
    AuthenticationActionType,
    AuthenticationRootState,
    Dependencies
> = (action$, state$, { persistence }) =>
    action$.pipe(
        filter(isActionOf(authenticationGetAsyncAction.request)),
        switchMap(() =>
            from(
                persistence.usersPersistence.getLoggedInUserInformation(),
            ).pipe(
                map((response) =>
                    authenticationGetAsyncAction.success(response),
                ),
                catchError((error: TwigApiError) =>
                    of(authenticationGetAsyncAction.failure(error)),
                ),
            ),
        ),
    );

export const authenticationEpics = [loadUserInformationEpic];
