import { ROUTE_PATH_CONFIG } from '../../../routes/constants';

export enum MenuOption {
    MAIN = 'MAIN',
    TAG_LIST = 'TAG_LIST',
}

export interface MenuOptionItem {
    displayName: string;
    path: string;
}

export const MENU_OPTION_ITEMS: Record<MenuOption, MenuOptionItem> = {
    [MenuOption.MAIN]: {
        displayName: 'Main',
        path: ROUTE_PATH_CONFIG.mainPage,
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
