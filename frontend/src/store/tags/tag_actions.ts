import { ListTagResponse } from 'proto/api/tag';
import { TwigApiError } from 'proto/api/twig_api_error';
import { ActionType, createAsyncAction } from 'typesafe-actions';

export const TAG_LIST = '@@tag/LIST';

export const tagListAsyncAction = createAsyncAction(
    `${TAG_LIST}_REQUEST`,
    `${TAG_LIST}_SUCCESS`,
    `${TAG_LIST}_FAILED`,
)<void, ListTagResponse, TwigApiError>();

export type TagActionType = ActionType<typeof tagListAsyncAction>;
