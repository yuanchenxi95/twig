import { Button, List, Popconfirm, Space } from 'antd';
import useModal from 'antd/es/modal/useModal';
import { Bookmark } from 'proto/api/bookmark';
import React, { useEffect, useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { Subject } from 'rxjs';
import {
    bookmarkCreateAsyncAction,
    bookmarkDeleteAsyncAction,
    bookmarkListAsyncAction,
} from '../../store/bookmarks/bookmark_actions';
import {
    selectBookmarkList,
    selectIsBookmarkLoading,
} from '../../store/bookmarks/bookmark_selectors';
import { EditBookmarkModal } from './edit_bookmark_modal/edit_bookmark_modal';

function DeleteBookmarkPopConfirm(props: { bookmark: Bookmark }) {
    const dispatch = useDispatch();

    const { bookmark } = props;
    return (
        <Popconfirm
            onConfirm={() => {
                dispatch(bookmarkDeleteAsyncAction.request(bookmark.id));
            }}
            title={`Do you want to delete the bookmark '${bookmark.displayName}'`}
        >
            <a>Delete</a>
        </Popconfirm>
    );
}

export function BookmarkList() {
    // TODO(yuanchenxi95) render the bookmark list
    const bookmarkList = useSelector(selectBookmarkList);
    const isBookmarkListLoading = useSelector(selectIsBookmarkLoading);
    const dispatch = useDispatch();
    const [onModalOpen$] = useState(new Subject<Bookmark>());

    useEffect(() => {
        // OnInit
        dispatch(bookmarkListAsyncAction.request({}));
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
            <List
                dataSource={bookmarkList}
                renderItem={(item) => (
                    <List.Item key={item.id}>
                        <List.Item.Meta
                            title={item.displayName}
                            description={item.url}
                        />
                        <DeleteBookmarkPopConfirm bookmark={item} />
                    </List.Item>
                )}
            />
        </div>
    );
}
