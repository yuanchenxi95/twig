import React from 'react';
import { Route, Switch } from 'react-router-dom';
import { NoMatch } from '../../components/no_match/no_match';
import { ROUTE_PATH_CONFIG } from '../../routes/constants';
import { TagListPage } from '../tag_list_page/tag_list_page';

export function AppRoutes() {
    return (
        <Switch>
            <Route exact path={ROUTE_PATH_CONFIG.mainPage}>
                <div>Main Page</div>
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
