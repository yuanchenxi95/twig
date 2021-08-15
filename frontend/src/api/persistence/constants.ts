export const API_REQUEST_MAPPING = {
    GET_USER_INFORMATION: '/api/users/me',

    // TAGS
    LIST_TAGS: '/api/tags',
    DELETE_TAG: (tagId: string) => `/api/tags/${tagId}`,
    CREATE_TAG: '/api/tags',

    // BOOKMARKS
    CREATE_BOOKMARK: '/api/bookmarks',
    UPDATE_TAG: (bookmarkId: string) => `/api/bookmarks/${bookmarkId}`,
};
