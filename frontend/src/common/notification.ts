import { notification } from 'antd/es';

export function showErrorNotification(errorMessage: string, title = '') {
    notification.error({
        message: title,
        description: errorMessage,
        duration: 0,
    });
}

export function showInfoNotification(
    message: string,
    title = '',
    duration = 5,
) {
    notification.info({
        message: title,
        description: message,
        duration,
    });
}
