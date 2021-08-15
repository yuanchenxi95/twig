import React from 'react';
import { Route, Switch } from 'react-router-dom';
import { NoMatch } from '../components/no_match/no_match';
import { BookmarkListPage } from '../containers/bookmark_list_page/bookmark_list_page';
import { TagListPage } from '../containers/tag_list_page/tag_list_page';
import { ROUTE_PATH_CONFIG } from './constants';

export function AppRoutes() {
    return (
        <Switch>
            <Route exact path={ROUTE_PATH_CONFIG.mainPage}>
                <div>Main Page</div>
            </Route>
            <Route path={ROUTE_PATH_CONFIG.bookmarksPage}>
                <BookmarkListPage />
            </Route>
            <Route path={ROUTE_PATH_CONFIG.tagsPage}>
                <TagListPage />
            </Route>
            <Route path={'*'}>
                <NoMatch />
            </Route>
        </Switch>
    );
}
