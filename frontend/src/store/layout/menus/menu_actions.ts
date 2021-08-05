import { ActionType, createAction } from 'typesafe-actions';
import { MenuOption } from './menu_constants';

export const LAYOUT_MENU_ACTION_TYPE = '@@layout_menu';

export const setLayoutMenuOption = createAction(
    `${LAYOUT_MENU_ACTION_TYPE}/SELECT_MENU_OPTION`,
)<MenuOption>();

export type LayoutMenuActionType = ActionType<typeof setLayoutMenuOption>;
