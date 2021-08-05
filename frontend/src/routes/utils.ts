import { PROTECTED_ROUTE } from './constants';

export function extractTopLevelRoutePath(pathname: string) {
    const headerPath = /^\/protected\/([^\/]*)\/?.*$/;
    const matchResult = pathname.match(headerPath);
    if (matchResult == null) {
        return null;
    }
    return `${PROTECTED_ROUTE}/${matchResult[1]}`;
}
