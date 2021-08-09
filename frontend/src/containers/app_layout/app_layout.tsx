import { Layout } from 'antd';
import { Content } from 'antd/es/layout/layout';
import React from 'react';
import { AppRoutes } from '../app_routes/app_routes_';
import { AppSider } from '../app_sider/app_sider';
import './app_layout.less';

export function AppLayout() {
    return (
        <Layout className={'app-layout'}>
            <AppSider />

            <Content className={'app-content'}>
                <AppRoutes />
            </Content>
        </Layout>
    );
}
