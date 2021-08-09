import { Popconfirm, Space } from 'antd';
import { Table } from 'antd/es';
import { ColumnsType } from 'antd/es/table/interface';
import { Tag } from 'proto/api/tag';
import React, { useEffect } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { TABLE_ACTION_COLUMN_WIDTH } from '../../constants/style';
import {
    tagDeleteAsyncAction,
    tagListAsyncAction,
} from '../../store/tags/tag_actions';
import {
    selectIsTagLoading,
    selectTagList,
} from '../../store/tags/tag_selectors';
import { extractIdFromItem } from '../../utils/item_key';
import { CreateTagModal } from './create_tag_modal/create_tag_modal';
import './tag_list.less';

function DeleteTagPopConfirm(props: { tag: Tag }) {
    const dispatch = useDispatch();

    const { tag } = props;
    return (
        <Popconfirm
            onConfirm={() => {
                dispatch(tagDeleteAsyncAction.request(tag.id));
            }}
            title={`Do you want to delete the tag '${tag.name}'`}
        >
            <a>Delete</a>
        </Popconfirm>
    );
}

const columns: ColumnsType<Tag> = [
    {
        title: 'Tag Name',
        render: (_, record) => <div>{record.name}</div>,
        sorter: (left, right) => left.name.localeCompare(right.name),
    },
    {
        title: 'Operation',
        width: TABLE_ACTION_COLUMN_WIDTH,
        render: (_, record) => <DeleteTagPopConfirm tag={record} />,
    },
];

export function TagList() {
    const tagList = useSelector(selectTagList);
    const isTagListLoading = useSelector(selectIsTagLoading);

    const dispatch = useDispatch();
    useEffect(() => {
        dispatch(tagListAsyncAction.request());
    }, []);

    return (
        <div className={'app-tag-list-container'}>
            <Space className={'app-table-header-action'}>
                <CreateTagModal />
            </Space>
            <Table
                bordered
                columns={columns}
                dataSource={tagList}
                loading={isTagListLoading}
                rowKey={extractIdFromItem}
            />
        </div>
    );
}
