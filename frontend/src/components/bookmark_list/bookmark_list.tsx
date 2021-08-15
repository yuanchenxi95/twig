import { Button, Space } from 'antd';
import useModal from 'antd/es/modal/useModal';
import { Bookmark } from 'proto/api/bookmark';
import React, { useEffect, useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { Subject } from 'rxjs';
import { bookmarkListAsyncAction } from '../../store/bookmarks/bookmark_actions';
import {
    selectBookmarkList,
    selectIsBookmarkLoading,
} from '../../store/bookmarks/bookmark_selectors';
import { EditBookmarkModal } from './edit_bookmark_modal/edit_bookmark_modal';

export function BookmarkList() {
    // TODO(yuanchenxi95) render the bookmark list
    // const bookmarkList = useSelector(selectBookmarkList);
    // const isBookmarkListLoading = useSelector(selectIsBookmarkLoading);
    const dispatch = useDispatch();
    const [onModalOpen$] = useState(new Subject<Bookmark>());

    useEffect(() => {
        // OnInit
        dispatch(bookmarkListAsyncAction.request());
        // OnDestroy
        return () => {
            onModalOpen$.complete();
        };
    }, []);

    return (
        <div className={'app-tag-list-container'}>
            <Space className={'app-table-header-action'}>
                <Button
                    type={'primary'}
                    onClick={() => {
                        onModalOpen$.next(Bookmark.fromPartial({}));
                    }}
                >
                    Create bookmark
                </Button>
            </Space>
            <EditBookmarkModal onModelOpen$={onModalOpen$} />
        </div>
    );
}
