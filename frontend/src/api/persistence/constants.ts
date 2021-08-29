export const API_REQUEST_MAPPING = {
    GET_USER_INFORMATION: '/api/users/me',

    // TAGS
    LIST_TAGS: '/api/tags',
    DELETE_TAG: (tagId: string) => `/api/tags/${tagId}`,
    CREATE_TAG: '/api/tags',

    // BOOKMARKS
    CREATE_BOOKMARK: '/api/bookmarks',
    LIST_BOOKMARK: (pageSize?: number | null, pageToken?: string | null) =>
        `/api/bookmarks?page_size=${pageSize ?? ''}&page_token=${
            pageToken ?? ''
        }`,
    UPDATE_BOOKMARK: (bookmarkId: string) => `/api/bookmarks/${bookmarkId}`,
    DELETE_BOOKMARK: (bookmarkId: string) => `/api/bookmarks/${bookmarkId}`,
};
