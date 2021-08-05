import { createReducer } from 'typesafe-actions';

import { LayoutMenuActionType, setLayoutMenuOption } from './menu_actions';
import { MenuOption } from './menu_constants';

export interface LayoutMenuState {
    selectedLayoutMenuOption: MenuOption;
}

export interface LayoutMenuRootState {
    layoutMenu: LayoutMenuState;
}

export const layoutMenuState: Readonly<LayoutMenuState> = {
    selectedLayoutMenuOption: MenuOption.MAIN,
};

export const layoutMenuReducer = createReducer<
    LayoutMenuState,
    LayoutMenuActionType
>(layoutMenuState).handleAction(setLayoutMenuOption, (state, action) => ({
    ...state,
    selectedLayoutMenuOption: action.payload,
}));
