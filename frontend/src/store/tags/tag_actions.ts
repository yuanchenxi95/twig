import {
    CreateTagRequest,
    CreateTagResponse,
    ListTagResponse,
} from 'proto/api/tag';
import { TwigApiError } from 'proto/api/twig_api_error';
import { ActionType, createAsyncAction } from 'typesafe-actions';

const TAG_KEY = '@@tag';
export const TAG_LIST = `${TAG_KEY}/LIST`;
export const TAG_DELETE = `${TAG_KEY}/DELETE`;
export const TAG_CREATE = `${TAG_KEY}/CREATE`;

export const tagListAsyncAction = createAsyncAction(
    `${TAG_LIST}_REQUEST`,
    `${TAG_LIST}_SUCCESS`,
    `${TAG_LIST}_FAILED`,
)<void, ListTagResponse, TwigApiError>();

export const tagDeleteAsyncAction = createAsyncAction(
    `${TAG_DELETE}_REQUEST`,
    `${TAG_DELETE}_SUCCESS`,
    `${TAG_DELETE}_FAILED`,
)<string, string, TwigApiError>();

export const tagCreateAsyncAction = createAsyncAction(
    `${TAG_CREATE}_REQUEST`,
    `${TAG_CREATE}_SUCCESS`,
    `${TAG_CREATE}_FAILED`,
)<CreateTagRequest, CreateTagResponse, TwigApiError>();

export type TagActionType = ActionType<
    | typeof tagListAsyncAction
    | typeof tagDeleteAsyncAction
    | typeof tagCreateAsyncAction
>;
