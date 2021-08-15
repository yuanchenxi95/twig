import { combineEpics, Epic } from 'redux-observable';
import { authenticationEpics } from './authentications/authentication_epics';
import { bookmarkEpics } from './bookmarks/bookmark_epics';
import { layoutMenuEpics } from './layout/menus/menu_epics';
import { RootAction } from './root_action';
import { tagEpics } from './tags/tag_epics';

const epics = [
    ...authenticationEpics,
    ...bookmarkEpics,
    ...tagEpics,
    ...layoutMenuEpics,
] as Array<Epic<RootAction, RootAction>>;

export const rootEpics = combineEpics(...epics);
