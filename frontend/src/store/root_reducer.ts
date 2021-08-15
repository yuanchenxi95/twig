import { RouterRootState } from 'connected-react-router';
import { combineReducers } from 'redux';

import {
    authenticationReducer,
    AuthenticationRootState,
} from './authentications/authentication_reducers';
import {
    bookmarkReducer,
    BookmarkRootState,
} from './bookmarks/bookmark_reducers';
import {
    layoutMenuReducer,
    LayoutMenuRootState,
} from './layout/menus/menu_reducers';
import { routerReducer } from './router/router_reducers';
import { tagReducer, TagRootState } from './tags/tag_reducers';

export type RootState =
    | AuthenticationRootState
    | BookmarkRootState
    | TagRootState
    | LayoutMenuRootState
    | RouterRootState;

export function createRootReducer() {
    return combineReducers<RootState>({
        authentication: authenticationReducer,
        bookmark: bookmarkReducer,
        tag: tagReducer,
        layoutMenu: layoutMenuReducer,
        router: routerReducer,
    });
}
