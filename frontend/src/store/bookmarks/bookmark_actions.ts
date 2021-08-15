import {
    CreateBookmarkRequest,
    CreateBookmarkResponse,
    ListBookmarkResponse,
} from 'proto/api/bookmark';
import { TwigApiError } from 'proto/api/twig_api_error';
import { ActionType, createAsyncAction } from 'typesafe-actions';

const BOOKMARK_KEY = '@@bookmark';
export const BOOKMARK_LIST = `${BOOKMARK_KEY}/LIST`;
export const BOOKMARK_DELETE = `${BOOKMARK_KEY}/DELETE`;
export const BOOKMARK_CREATE = `${BOOKMARK_KEY}/CREATE`;

export const bookmarkListAsyncAction = createAsyncAction(
    `${BOOKMARK_LIST}_REQUEST`,
    `${BOOKMARK_LIST}_SUCCESS`,
    `${BOOKMARK_LIST}_FAILED`,
)<void, ListBookmarkResponse, TwigApiError>();

export const bookmarkDeleteAsyncAction = createAsyncAction(
    `${BOOKMARK_DELETE}_REQUEST`,
    `${BOOKMARK_DELETE}_SUCCESS`,
    `${BOOKMARK_DELETE}_FAILED`,
)<string, string, TwigApiError>();

export const bookmarkCreateAsyncAction = createAsyncAction(
    `${BOOKMARK_CREATE}_REQUEST`,
    `${BOOKMARK_CREATE}_SUCCESS`,
    `${BOOKMARK_CREATE}_FAILED`,
)<CreateBookmarkRequest, CreateBookmarkResponse, TwigApiError>();

export type BookmarkActionType = ActionType<
    | typeof bookmarkListAsyncAction
    | typeof bookmarkDeleteAsyncAction
    | typeof bookmarkCreateAsyncAction
>;
