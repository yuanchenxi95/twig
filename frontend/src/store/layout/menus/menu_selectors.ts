// import { createSelector } from 'reselect';

import { createSelector } from 'reselect';
import { LayoutMenuRootState, layoutMenuState } from './menu_reducers';

export const selectLayoutMenuState = (state: LayoutMenuRootState) =>
    state.layoutMenu ?? layoutMenuState;

export const selectSelectedLayoutMenuOption = createSelector(
    selectLayoutMenuState,
    (state) => state.selectedLayoutMenuOption,
);
