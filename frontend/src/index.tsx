import { ConnectedRouter } from 'connected-react-router';
import React from 'react';
import ReactDOM from 'react-dom';
import { Provider } from 'react-redux';
import { App } from './app';
import { store } from './store';
import { hashHistory } from './store/router/hash_history';

// eslint-disable-next-line no-undef
const rootElement = document.getElementById('twig-app');

if (rootElement) {
    ReactDOM.render(
        <Provider store={store}>
            <ConnectedRouter history={hashHistory}>
                <App />
            </ConnectedRouter>
        </Provider>,
        rootElement,
    );
}
