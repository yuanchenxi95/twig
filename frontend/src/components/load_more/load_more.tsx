import { Button } from 'antd';
import React from 'react';

import './load_more.less';

export function LoadMore(props: {
    showLoadMore: boolean;
    onLoadMore: () => void;
}) {
    const { showLoadMore, onLoadMore } = props;
    if (showLoadMore === false) {
        return null;
    }
    return (
        <div className={'app-load-more'}>
            <Button onClick={onLoadMore}>Load more</Button>
        </div>
    );
}
