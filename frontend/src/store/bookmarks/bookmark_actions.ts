import {
    Bookmark,
    CreateBookmarkRequest,
    CreateBookmarkResponse,
    ListBookmarkRequest,
    ListBookmarkResponse,
    UpdateBookmarkResponse,
} from 'proto/api/bookmark';
import { TwigApiError } from 'proto/api/twig_api_error';
import { ActionType, createAsyncAction } from 'typesafe-actions';

export const BOOKMARK_LIST = `@@bookmark/LIST`;
export const BOOKMARK_DELETE = `@@bookmark/DELETE`;
export const BOOKMARK_CREATE = `@@bookmark/CREATE`;
export const BOOKMARK_UPDATE = `@@bookmark/UPDATE`;

export const bookmarkListAsyncAction = createAsyncAction(
    `${BOOKMARK_LIST}_REQUEST`,
    `${BOOKMARK_LIST}_SUCCESS`,
    `${BOOKMARK_LIST}_FAILED`,
)<
    ListBookmarkRequest,
    {
        nextPageToken: null | string;
        listBookmarkResponse: ListBookmarkResponse;
    },
    TwigApiError
>();

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

export const bookmarkUpdateAsyncAction = createAsyncAction(
    `${BOOKMARK_UPDATE}_REQUEST`,
    `${BOOKMARK_UPDATE}_SUCCESS`,
    `${BOOKMARK_UPDATE}_FAILED`,
)<Bookmark, UpdateBookmarkResponse, TwigApiError>();

export type BookmarkActionType = ActionType<
    | typeof bookmarkListAsyncAction
    | typeof bookmarkDeleteAsyncAction
    | typeof bookmarkCreateAsyncAction
    | typeof bookmarkUpdateAsyncAction
>;
