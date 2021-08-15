import { Select } from 'antd';
import { Form, Input, Modal } from 'antd/es';
import { useForm } from 'antd/es/form/Form';
import { Bookmark, CreateBookmarkRequest } from 'proto/api/bookmark';
import React, { useEffect, useState } from 'react';
import { useDispatch, useSelector, useStore } from 'react-redux';
import {
    distinctUntilChanged,
    filter,
    from,
    map,
    Observable,
    take,
} from 'rxjs';
import {
    EditType,
    getEditType,
    getEditTypeDisplayName,
} from '../../../common/edit_type';
import { LoadingState } from '../../../common/loading_state';
import { showInfoNotification } from '../../../common/notification';
import { bookmarkCreateAsyncAction } from '../../../store/bookmarks/bookmark_actions';
import { selectBookmarkLoadingState } from '../../../store/bookmarks/bookmark_selectors';
import { selectTagList } from '../../../store/tags/tag_selectors';

enum EditBookmarkFormField {
    BOOKMARK_URL = 'BOOKMARK_URL',
    NAME = 'NAME',
    TAGS = 'TAGS',
}

interface EditBookmarkModalProps {
    onClose?: (saved: boolean) => void;
    onModelOpen$: Observable<Bookmark>;
}

export const EditBookmarkModal: React.FC<EditBookmarkModalProps> = ({
    onClose,
    onModelOpen$,
}) => {
    const [visible, setVisible] = useState(false);
    const [editType, setEditType] = useState(EditType.CREATE);
    const [form] = useForm();

    const dispatch = useDispatch();
    const store = useStore();
    const tagList = useSelector(selectTagList);

    useEffect(() => {
        // OnInit
        const subscription = onModelOpen$.subscribe((bookmark) => {
            setVisible(true);
            form.resetFields();
            form.setFieldsValue({
                [EditBookmarkFormField.BOOKMARK_URL]: bookmark.url,
            });
            form.setFieldsValue({
                [EditBookmarkFormField.NAME]: bookmark.displayName,
            });
            form.setFieldsValue({
                [EditBookmarkFormField.TAGS]: bookmark.tags,
            });
            setEditType(getEditType(bookmark));
        });

        // OnDestroy
        return () => {
            subscription.unsubscribe();
        };
    }, []);

    const handleClose = (saved: boolean) => {
        form.resetFields();
        setVisible(false);
        if (onClose != null) {
            onClose(saved);
        }
    };

    const handleCancel = () => {
        handleClose(false);
    };

    const handleOk = async () => {
        const values = await form.validateFields();
        const bookmarkToReturn = Bookmark.fromPartial({});
        bookmarkToReturn.url = values[EditBookmarkFormField.BOOKMARK_URL];
        bookmarkToReturn.displayName = values[EditBookmarkFormField.NAME];
        bookmarkToReturn.tags = values[EditBookmarkFormField.TAGS];
        const createBookmarkRequest: CreateBookmarkRequest = {
            bookmark: bookmarkToReturn,
        };
        dispatch(bookmarkCreateAsyncAction.request(createBookmarkRequest));

        from(store)
            .pipe(
                map((state) => selectBookmarkLoadingState(state)),
                distinctUntilChanged(),
                filter((loadingState) => loadingState !== LoadingState.LOADING),
                take(1),
            )
            .subscribe((loadingState) => {
                if (loadingState === LoadingState.SUCCEEDED) {
                    showInfoNotification(
                        `Bookmark ${
                            editType === EditType.CREATE ? 'created' : 'updated'
                        } successfully.`,
                    );
                    handleClose(true);
                }
            });
    };

    return (
        <>
            <Modal
                centered
                onCancel={handleCancel}
                onOk={handleOk}
                okText={getEditTypeDisplayName(editType)}
                title={`${getEditTypeDisplayName(editType)} Bookmark`}
                visible={visible}
            >
                <Form form={form}>
                    <Form.Item
                        label={'URL'}
                        name={EditBookmarkFormField.BOOKMARK_URL}
                        rules={[
                            {
                                required: true,
                                message: 'Please input the url.',
                            },
                        ]}
                    >
                        <Input autoComplete={'off'} />
                    </Form.Item>
                    <Form.Item label={'Name'} name={EditBookmarkFormField.NAME}>
                        <Input autoComplete={'off'} />
                    </Form.Item>
                    <Form.Item label={'Tags'} name={EditBookmarkFormField.TAGS}>
                        <Select mode={'tags'} placeholder={'Enter tags'}>
                            {tagList.map((tag) => (
                                <Select.Option key={tag.id} value={tag.name}>
                                    {tag.name}
                                </Select.Option>
                            ))}
                        </Select>
                    </Form.Item>
                </Form>
            </Modal>
        </>
    );
};
