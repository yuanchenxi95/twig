import { DeleteFilled, EditFilled } from '@ant-design/icons';
import { Button, List, Popconfirm, Space, Tag, Tooltip } from 'antd';
import { Bookmark } from 'proto/api/bookmark';
import React, { useEffect, useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { Subject } from 'rxjs';
import { LoadingState } from '../../common/loading_state';
import {
    bookmarkDeleteAsyncAction,
    bookmarkListAsyncAction,
} from '../../store/bookmarks/bookmark_actions';
import {
    selectBookmarkList,
    selectBookmarkListNextPageToken,
    selectBookmarkLoadingState,
    selectIsBookmarkLoading,
} from '../../store/bookmarks/bookmark_selectors';
import { openInNewTab } from '../../utils/open_in_new_tab';
import { LoadMore } from '../load_more/load_more';
import './bookmark_list.less';
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
            <Button type="link" icon={<DeleteFilled />}>
                Delete
            </Button>
        </Popconfirm>
    );
}

export function BookmarkList() {
    const bookmarkList = useSelector(selectBookmarkList);
    const isBookmarkListLoading = useSelector(selectIsBookmarkLoading);
    const bookmarkLoadingState = useSelector(selectBookmarkLoadingState);
    const bookmarkListNextPageToken = useSelector(
        selectBookmarkListNextPageToken,
    );
    const dispatch = useDispatch();
    const [onModalOpen$] = useState(new Subject<Bookmark>());

    const loadMore = () => {
        if (bookmarkListNextPageToken != null) {
            dispatch(
                bookmarkListAsyncAction.request({
                    pageToken: bookmarkListNextPageToken,
                }),
            );
        }
    };

    useEffect(() => {
        // OnInit
        dispatch(bookmarkListAsyncAction.request({}));
        // OnDestroy
        return () => {
            onModalOpen$.complete();
        };
    }, []);

    return (
        <div className={'app-bookmark-list-container'}>
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
            <div className={'bookmark-list'}>
                <List
                    loading={isBookmarkListLoading}
                    itemLayout={'vertical'}
                    loadMore={
                        <LoadMore
                            showLoadMore={
                                bookmarkListNextPageToken != null &&
                                bookmarkLoadingState == LoadingState.SUCCEEDED
                            }
                            onLoadMore={loadMore}
                        />
                    }
                    size={'large'}
                    dataSource={bookmarkList}
                    renderItem={(item) => (
                        <List.Item
                            key={item.id}
                            extra={
                                <div>
                                    <Button
                                        key={'edit'}
                                        type="link"
                                        icon={<EditFilled />}
                                        onClick={() => {
                                            onModalOpen$.next(item);
                                        }}
                                    >
                                        Edit
                                    </Button>
                                    <DeleteBookmarkPopConfirm bookmark={item} />
                                </div>
                            }
                        >
                            <List.Item.Meta
                                title={
                                    <Tooltip title={item.url}>
                                        <div
                                            className={'bookmark-link'}
                                            onClick={() => {
                                                openInNewTab(item.url);
                                            }}
                                        >
                                            {item.url}
                                        </div>
                                    </Tooltip>
                                }
                                description={item.displayName}
                            />

                            <div>
                                {item.tags.map((tag) => {
                                    return <Tag>{tag}</Tag>;
                                })}
                            </div>
                        </List.Item>
                    )}
                />
            </div>
        </div>
    );
}
