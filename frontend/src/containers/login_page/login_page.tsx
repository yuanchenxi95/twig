import { Button } from 'antd';
import React from 'react';
import { useSelector } from 'react-redux';
import { Redirect } from 'react-router-dom';
import { LOGIN_PATH, ROUTE_PATH_CONFIG } from '../../routes/constants';
import { selectIsUserLoggedIn } from '../../store/authentications/authentication_selectors';

export function LoginPage() {
    const isUserLoggedIn = useSelector(selectIsUserLoggedIn);

    if (isUserLoggedIn === true) {
        return <Redirect to={ROUTE_PATH_CONFIG.mainPage} />;
    } else {
        return (
            <>
                <Button type={'link'}>
                    <a href={LOGIN_PATH}>Login with Github</a>
                </Button>
            </>
        );
    }
}
