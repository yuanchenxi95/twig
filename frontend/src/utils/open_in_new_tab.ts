import { API_REQUEST_MAPPING } from '../api/persistence/constants';

export function openInNewTab(url: string) {
    window.open(API_REQUEST_MAPPING.REDIRECTION(url), '_blank');
}
