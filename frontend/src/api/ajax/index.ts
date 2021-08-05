import {
    EnvironmentVariable,
    getEnvironmentVariable,
} from '../../constants/environment';
import { AjaxClient } from './ajax_client';

const API_ENDPOINT = getEnvironmentVariable(EnvironmentVariable.API_ENDPOINT);
export const ajaxClient = new AjaxClient(API_ENDPOINT);
