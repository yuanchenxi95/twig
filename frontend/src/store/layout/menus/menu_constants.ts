import { ROUTE_PATH_CONFIG } from '../../../routes/constants';

export enum MenuOption {
    MAIN = 'MAIN',
    TAG_LIST = 'TAG_LIST',
    BOOKMARK_LIST = 'BOOKMARK_LIST',
}

export interface MenuOptionItem {
    displayName: string;
    path: string;
}

export const MENU_OPTIONS: ReadonlyArray<MenuOption> = [
    MenuOption.MAIN,
    MenuOption.BOOKMARK_LIST,
    MenuOption.TAG_LIST,
];

export const MENU_OPTION_ITEMS: Readonly<Record<MenuOption, MenuOptionItem>> = {
    [MenuOption.MAIN]: {
        displayName: 'Main',
        path: ROUTE_PATH_CONFIG.mainPage,
    },
    [MenuOption.BOOKMARK_LIST]: {
        displayName: 'Bookmarks',
        path: ROUTE_PATH_CONFIG.bookmarksPage,
    },
    [MenuOption.TAG_LIST]: {
        displayName: 'Tags',
        path: ROUTE_PATH_CONFIG.tagsPage,
    },
};

export const PATH_MENU_OPTION_MAP = new Map<string, MenuOption>();
for (const [headerOption, item] of Object.entries(MENU_OPTION_ITEMS)) {
    PATH_MENU_OPTION_MAP.set(item.path, headerOption as MenuOption);
}
