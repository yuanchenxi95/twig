package com.yuanchenxi95.twig.constants

enum class ResourceType {
    TAG,
    BOOKMARK,
}

private fun keyPluralFmt(count: Int, singular: String, plural: String): String {
    return if (count == 1) singular else plural
}

fun resourceTypeFormat(resourceType: ResourceType, count: Int): String {
    return when (resourceType) {
        ResourceType.BOOKMARK -> keyPluralFmt(count, "bookmark", "bookmarks")
        ResourceType.TAG -> keyPluralFmt(count, "tag", "tags")
    }
}

fun resourceTypeSingularFormat(resourceType: ResourceType): String {
    return resourceTypeFormat(resourceType, 1)
}
