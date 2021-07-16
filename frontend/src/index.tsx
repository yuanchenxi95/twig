import React from 'react';
import ReactDOM from 'react-dom';
import { App } from './app';

// eslint-disable-next-line no-undef
const rootElement = document.getElementById('twig-app');

if (rootElement) {
    ReactDOM.render(<App />, rootElement);
}
