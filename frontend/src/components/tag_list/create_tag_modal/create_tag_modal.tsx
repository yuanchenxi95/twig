import { Button, Form, Input, Modal } from 'antd/es';
import { useForm } from 'antd/es/form/Form';
import { CreateTagRequest } from 'proto/api/tag';
import React, { useState } from 'react';
import { useDispatch, useSelector, useStore } from 'react-redux';
import { distinctUntilChanged, filter, from, map, take } from 'rxjs';
import { LoadingState } from '../../../common/loading_state';
import { tagCreateAsyncAction } from '../../../store/tags/tag_actions';
import {
    selectIsTagLoading,
    selectTagLoadingState,
} from '../../../store/tags/tag_selectors';
import { LoadingSpinner } from '../../loading_spinner/loading_spinner';

const TAG_NAME_FIELD = 'tagName';

export function CreateTagModal() {
    const [isModalVisible, setIsModalVisible] = useState(false);
    const [form] = useForm();
    const dispatch = useDispatch();
    const store = useStore();
    const isTagLoading = useSelector(selectIsTagLoading);

    const showModal = () => {
        setIsModalVisible(true);
    };

    const resetFormAndExit = () => {
        form.resetFields();
        setIsModalVisible(false);
    };

    const handleOk = async () => {
        try {
            const values = await form.validateFields();
            const createTagRequest: CreateTagRequest = {
                name: values[TAG_NAME_FIELD],
            };
            dispatch(tagCreateAsyncAction.request(createTagRequest));

            from(store)
                .pipe(
                    map((state) => selectTagLoadingState(state)),
                    distinctUntilChanged(),
                    filter(
                        (loadingState) => loadingState !== LoadingState.LOADING,
                    ),
                    take(1),
                )
                .subscribe((loadingState) => {
                    if (loadingState === LoadingState.SUCCEEDED) {
                        resetFormAndExit();
                    }
                });
        } catch (error) {}
    };

    const handleCancel = () => {
        resetFormAndExit();
    };
    return (
        <>
            <Button type={'primary'} onClick={showModal}>
                Create Tag
            </Button>

            <Modal
                centered
                confirmLoading={isTagLoading}
                onCancel={handleCancel}
                onOk={handleOk}
                title={'Create Tag'}
                visible={isModalVisible}
            >
                <LoadingSpinner loading={isTagLoading}>
                    <Form form={form}>
                        <Form.Item
                            label={'Tag name'}
                            name={TAG_NAME_FIELD}
                            rules={[
                                {
                                    required: true,
                                    message: 'Please input the tag name.',
                                },
                            ]}
                        >
                            <Input />
                        </Form.Item>
                    </Form>
                </LoadingSpinner>
            </Modal>
        </>
    );
}
