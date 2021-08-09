import { Spin } from 'antd';
import React from 'react';

export const LoadingSpinner: React.FC<{
    loading: boolean;
}> = (props) => {
    return (
        <Spin tip={'Loading'} spinning={props.loading}>
            {props.children}
        </Spin>
    );
};
