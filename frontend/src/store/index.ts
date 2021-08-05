import { routerMiddleware } from 'connected-react-router';
import { applyMiddleware, createStore } from 'redux';
import { createEpicMiddleware } from 'redux-observable';

import { ajaxClient } from '../api/ajax';
import { getRootPersistence } from '../api/persistence';

import { Dependencies } from './dependencies';
import { RootAction } from './root_action';
import { rootEpics } from './root_epic';
import { createRootReducer, RootState } from './root_reducer';
import { hashHistory } from './router/hash_history';
import { composeEnhancers } from './utils';

export const epicMiddleware = createEpicMiddleware<
    RootAction,
    RootAction,
    RootState,
    Dependencies
>({
    dependencies: {
        persistence: getRootPersistence(ajaxClient),
    },
});

// configure middlewares
const middlewares = [routerMiddleware(hashHistory), epicMiddleware];
// compose enhancers
const enhancer = composeEnhancers(applyMiddleware(...middlewares));

// rehydrate state on app start
const initialState = {};

// create store
export const store = createStore(createRootReducer(), initialState, enhancer);

epicMiddleware.run(rootEpics);
