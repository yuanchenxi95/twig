import React from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { Redirect, Route, Switch } from 'react-router-dom';
import { LoadingState } from '../common/loading_state';
import { AppLayout } from '../containers/app_layout/app_layout';
import { LoginPage } from '../containers/login_page/login_page';
import { authenticationGetAsyncAction } from '../store/authentications/authentication_actions';
import {
    selectAuthenticationLoadingState,
    selectIsUserLoggedIn,
} from '../store/authentications/authentication_selectors';
import { PROTECTED_ROUTE, ROUTE_PATH_CONFIG } from './constants';

const PrivateRoute: React.FC<{ path: string }> = ({ children, ...rest }) => {
    const isUserLoggedIn = useSelector(selectIsUserLoggedIn);

    return (
        <Route
            {...rest}
            render={({ location }) =>
                isUserLoggedIn ? (
                    children
                ) : (
                    <Redirect
                        to={{
                            pathname: ROUTE_PATH_CONFIG.loginPage,
                            state: { from: location },
                        }}
                    />
                )
            }
        />
    );
};

export function RootRoute() {
    const authenticationLoadingState = useSelector(
        selectAuthenticationLoadingState,
    );
    const dispatch = useDispatch();

    if (authenticationLoadingState === LoadingState.IDLE) {
        dispatch(authenticationGetAsyncAction.request());
    }

    if (
        authenticationLoadingState === LoadingState.LOADING ||
        authenticationLoadingState === LoadingState.IDLE
    ) {
        return <h1>Loading</h1>;
    }

    return (
        <Switch>
            <PrivateRoute path={PROTECTED_ROUTE}>
                <AppLayout />
            </PrivateRoute>
            <Route path={ROUTE_PATH_CONFIG.loginPage}>
                <LoginPage />
            </Route>
            <Route path={'*'}>
                <Redirect to={ROUTE_PATH_CONFIG.loginPage} />
            </Route>
        </Switch>
    );
}
