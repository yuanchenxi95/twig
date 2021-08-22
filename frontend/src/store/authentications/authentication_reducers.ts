import { GetUserInformationResponse } from 'proto/api/user';
import { createReducer } from 'typesafe-actions';
import { LoadingState } from '../../common/loading_state';

import {
    AuthenticationActionType,
    authenticationGetAsyncAction,
    authenticationResetAction,
} from './authentication_actions';

export interface AuthenticationState {
    userInformation: GetUserInformationResponse | null;
    loadingState: LoadingState;
}

export interface AuthenticationRootState {
    authentication: AuthenticationState;
}

export const authenticationState: Readonly<AuthenticationState> = {
    userInformation: null,
    loadingState: LoadingState.IDLE,
};

export const authenticationReducer = createReducer<
    AuthenticationState,
    AuthenticationActionType
>(authenticationState)
    .handleAction(authenticationGetAsyncAction.request, (state) => ({
        ...state,
        loadingState: LoadingState.LOADING,
    }))
    .handleAction(authenticationGetAsyncAction.success, (state, action) => ({
        ...state,
        userInformation: action.payload,
        loadingState: LoadingState.SUCCEEDED,
    }))
    .handleAction(authenticationGetAsyncAction.failure, (state) => ({
        ...state,
        userInformation: null,
        loadingState: LoadingState.FAILED,
    }))
    // TODO(yuanchenxi95) call rpc to remove the token in redis.
    .handleAction(authenticationResetAction, (state) => ({
        ...state,
        userInformation: null,
        loadingState: LoadingState.SUCCEEDED,
    }));
