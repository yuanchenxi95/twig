import { PageHeader } from 'antd/es';
import React from 'react';
import { TagList } from '../../components/tag_list/tag_list';

export function TagListPage() {
    return (
        <div className={'app-page-container'}>
            <PageHeader className="app-page-header" title="Tag list" />
            <TagList />
        </div>
    );
}
