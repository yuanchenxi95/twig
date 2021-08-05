import {
    LOCATION_CHANGE,
    LocationChangeAction,
    RouterRootState,
} from 'connected-react-router';
import { Epic } from 'redux-observable';

import { filter, of, switchMap } from 'rxjs';
import { extractTopLevelRoutePath } from '../../../routes/utils';
import { Dependencies } from '../../dependencies';
import { LayoutMenuActionType, setLayoutMenuOption } from './menu_actions';
import { MenuOption, PATH_MENU_OPTION_MAP } from './menu_constants';

export const updateSelectedMenuOptionEpic: Epic<
    LayoutMenuActionType | LocationChangeAction,
    LayoutMenuActionType | LocationChangeAction,
    RouterRootState,
    Dependencies
> = (action$) =>
    action$.pipe(
        filter(
            (action): action is LocationChangeAction =>
                action.type === LOCATION_CHANGE,
        ),
        switchMap((action) => {
            const pathname = action.payload.location?.pathname ?? '';
            const topLevelPath = extractTopLevelRoutePath(pathname);
            let selectedHeaderOption = MenuOption.MAIN;
            if (topLevelPath != null) {
                const option = PATH_MENU_OPTION_MAP.get(topLevelPath);
                if (option != null) {
                    selectedHeaderOption = option;
                }
            }
            return of(setLayoutMenuOption(selectedHeaderOption));
        }),
    );

export const layoutMenuEpics = [updateSelectedMenuOptionEpic];
