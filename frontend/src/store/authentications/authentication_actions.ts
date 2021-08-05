import { TwigApiError } from 'proto/api/twig_api_error';
import { GetUserInformationResponse } from 'proto/api/user';
import { ActionType, createAction, createAsyncAction } from 'typesafe-actions';

export const AUTHENTICATION_GET = '@@authentication/GET';

export const authenticationGetAsyncAction = createAsyncAction(
    `${AUTHENTICATION_GET}_REQUEST`,
    `${AUTHENTICATION_GET}_SUCCESS`,
    `${AUTHENTICATION_GET}_FAILED`,
)<void, GetUserInformationResponse, TwigApiError>();

export const authenticationResetAction = createAction(
    '@@authentication/RESET',
)<void>();

export type AuthenticationActionType = ActionType<
    typeof authenticationGetAsyncAction | typeof authenticationResetAction
>;
