import { Layout } from 'antd';
import React from 'react';
import './app.less';
import { AppHeader } from './containers/app_header/app_header';
import { RootRoute } from './routes/routes';

export function App() {
    return (
        <Layout className={'app-layout'}>
            <AppHeader />
            <RootRoute />
        </Layout>
    );
}
