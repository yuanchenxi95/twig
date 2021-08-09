import { message } from 'antd/es';

export function showErrorNotification(errorMessage: string) {
    message.error(errorMessage).then();
}
