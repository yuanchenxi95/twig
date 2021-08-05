import { AjaxClient } from '../ajax/ajax_client';
import { TagsPersistence } from './tags_persistence';
import { UsersPersistence } from './users_persistence';

export interface RootPersistence {
    usersPersistence: UsersPersistence;
    tagsPersistence: TagsPersistence;
}

export function getRootPersistence(client: AjaxClient): RootPersistence {
    return {
        usersPersistence: new UsersPersistence(client),
        tagsPersistence: new TagsPersistence(client),
    };
}
