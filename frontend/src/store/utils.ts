import { compose } from 'redux';

export const composeEnhancers =
    (process.env.NODE_ENV === 'development' &&
        window &&
        // eslint-disable-next-line @typescript-eslint/ban-ts-comment
        // @ts-ignore
        window.__REDUX_DEVTOOLS_EXTENSION_COMPOSE__) ||
    compose;
