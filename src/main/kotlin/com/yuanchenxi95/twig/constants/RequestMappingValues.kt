package com.yuanchenxi95.twig.constants

class RequestMappingValues {
    companion object {
        const val BOOKMARK_ID = "bookmarkId"
        const val TAG_ID = "tagId"

        const val APP_PATH = "/app"

        const val CREATE_BOOKMARK = "/api/bookmarks"
        const val LIST_BOOKMARK = "/api/bookmarks"
        const val UPDATE_BOOKMARK = "/api/bookmarks/{$BOOKMARK_ID}"

        const val LIST_TAG = "/api/tags"
        const val CREATE_TAG = "/api/tags"
        const val DELETE_TAG = "/api/tags/{$TAG_ID}"

        const val GET_USER_INFORMATION = "/api/users/me"

        const val SIGN_OUT = "/api/authentication/signout"
        const val AUTHENTICATION_CALLBACK = "/api/authentication/callback"
    }
}
