import { AjaxClient } from '../ajax/ajax_client';
import { BookmarksPersistence } from './bookmarks_persistence';
import { TagsPersistence } from './tags_persistence';
import { UsersPersistence } from './users_persistence';

export interface RootPersistence {
    usersPersistence: UsersPersistence;
    tagsPersistence: TagsPersistence;
    bookmarksPersistence: BookmarksPersistence;
}

export function getRootPersistence(client: AjaxClient): RootPersistence {
    return {
        usersPersistence: new UsersPersistence(client),
        tagsPersistence: new TagsPersistence(client),
        bookmarksPersistence: new BookmarksPersistence(client),
    };
}
