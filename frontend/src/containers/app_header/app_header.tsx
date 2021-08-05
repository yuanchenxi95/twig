import { Button, Typography } from 'antd';
import { Header } from 'antd/es/layout/layout';
import React from 'react';
import { useDispatch } from 'react-redux';
import { authenticationResetAction } from '../../store/authentications/authentication_actions';
import './app_header.less';

export function AppHeader() {
    const dispatch = useDispatch();
    return (
        <Header className={'app-header'}>
            <div className={'app-icon'}>
                <Typography.Text className={'app-icon-text'}>
                    Twig
                </Typography.Text>
            </div>
            <div className={'header-space'} />
            <div>
                <Button
                    type={'text'}
                    danger
                    onClick={() => dispatch(authenticationResetAction())}
                >
                    Sign out
                </Button>
            </div>
        </Header>
    );
}
