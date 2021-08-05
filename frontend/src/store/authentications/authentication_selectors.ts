// import { createSelector } from 'reselect';

import { createSelector } from 'reselect';
import { LoadingState } from '../../common/loading_state';
import {
    AuthenticationRootState,
    authenticationState,
} from './authentication_reducers';

export const selectAuthenticationState = (state: AuthenticationRootState) =>
    state.authentication ?? authenticationState;

export const selectIsUserLoggedIn = createSelector(
    selectAuthenticationState,
    (state) =>
        state.loadingState === LoadingState.SUCCEEDED &&
        state.userInformation != null,
);

export const selectIsLoadingAuthentication = createSelector(
    selectAuthenticationState,
    (state) => state.loadingState === LoadingState.LOADING,
);

export const selectAuthenticationLoadingState = createSelector(
    selectAuthenticationState,
    (state) => state.loadingState,
);

export const selectAuthenticationLoaded = createSelector(
    selectAuthenticationState,
    (state) =>
        state.loadingState === LoadingState.SUCCEEDED ||
        state.loadingState === LoadingState.FAILED,
);

export const selectUserName = createSelector(
    selectAuthenticationState,
    (state) => state.userInformation?.name,
);
