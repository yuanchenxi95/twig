package com.yuanchenxi95.twig.constants

class ErrorMessageUtils {
    companion object {
        fun resourceNotFoundError(id: String, resourceType: ResourceType): String {
            val resourceTypeDisplayName = resourceTypeSingularFormat(resourceType)
            return "Resource $resourceTypeDisplayName with ID '$id' not found."
        }
    }
}
