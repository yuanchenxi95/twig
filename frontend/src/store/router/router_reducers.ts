import { connectRouter } from 'connected-react-router';
import { hashHistory } from './hash_history';

export const routerReducer = connectRouter(hashHistory);
