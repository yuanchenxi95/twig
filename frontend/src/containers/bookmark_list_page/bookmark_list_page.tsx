import { PageHeader } from 'antd/es';
import React from 'react';
import { BookmarkList } from '../../components/bookmark_list/bookmark_list';

export function BookmarkListPage() {
    return (
        <div className={'app-page-container'}>
            <PageHeader className="app-page-header" title="Bookmark list" />
            <div className={'app-page-body'}>
                <BookmarkList />
            </div>
        </div>
    );
}
