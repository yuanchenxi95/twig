import { LoadingOutlined } from '@ant-design/icons';
import { Modal, Spin } from 'antd';
import React from 'react';
import styled from 'styled-components';

const FullSizeCenteredDiv = styled.div`
    height: 100%;
    width: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
`;

const LoadingIcon = <LoadingOutlined style={{ fontSize: 48 }} spin />;

export const BlockingLoadingSpinner: React.FC<{
    loading: boolean;
}> = (props) => {
    if (props.loading !== true) {
        return <></>;
    }

    return (
        <Modal
            visible
            title={null}
            footer={null}
            centered
            modalRender={() => (
                <FullSizeCenteredDiv>
                    <Spin indicator={LoadingIcon}>{props.children}</Spin>
                </FullSizeCenteredDiv>
            )}
        />
    );
};
