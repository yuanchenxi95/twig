import { AuthenticationActionType } from './authentications/authentication_actions';
import { LayoutMenuActionType } from './layout/menus/menu_actions';
import { TagActionType } from './tags/tag_actions';

export type RootAction =
    | AuthenticationActionType
    | TagActionType
    | LayoutMenuActionType;
