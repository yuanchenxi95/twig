package com.yuanchenxi95.twig.utils.protobufutils

import com.google.common.base.Splitter
import com.google.errorprone.annotations.CanIgnoreReturnValue
import com.google.protobuf.Descriptors
import com.google.protobuf.FieldMask
import com.google.protobuf.Message
import com.google.protobuf.util.FieldMaskUtil
import com.google.protobuf.util.FieldMaskUtil.MergeOptions
import java.util.*
import java.util.logging.Logger

private val DEFAULT_OPTION = MergeOptions().setReplaceRepeatedFields(true)

/**
 * FieldMaskTree copied from com.google.protobuf.util.FieldMaskTree
 * Taken from: https://github.com/protocolbuffers/protobuf/blob/8a3c4948a49d3b38effea499fd9dee66f28cb0c4/java/util/src/main/java/com/google/protobuf/util/FieldMaskTree.java
 */
class FieldMaskTree(mask: FieldMask?) {
    class Node() {
        val children: SortedMap<String, Node> = TreeMap()
    }

    private val root = Node()

    init {
        if (mask != null) {
            mergeFromFieldMask(mask)
        }
    }

    override fun toString(): String {
        return FieldMaskUtil.toString(toFieldMask())
    }

    /**
     * Adds a field path to the tree. In a FieldMask, every field path matches the specified field as
     * well as all its sub-fields. For example, a field path "foo.bar" matches field "foo.bar" and
     * also "foo.bar.baz", etc. When adding a field path to the tree, redundant sub-paths will be
     * removed. That is, after adding "foo.bar" to the tree, "foo.bar.baz" will be removed if it
     * exists, which will turn the tree node for "foo.bar" to a leaf node. Likewise, if the field path
     * to add is a sub-path of an existing leaf node, nothing will be changed in the tree.
     */
    @CanIgnoreReturnValue
    fun addFieldPath(path: String): FieldMaskTree {
        val parts: Array<String> =
            path.split(FIELD_PATH_SEPARATOR_REGEX).toTypedArray()
        if (parts.isEmpty()) {
            return this
        }
        var node: Node? = root
        var createNewBranch = false
        // Find the matching node in the tree.
        for (part: String? in parts) {
            // Check whether the path matches an existing leaf node.
            if (!createNewBranch && node != root && node!!.children.isEmpty()) {
                // The path to add is a sub-path of an existing leaf node.
                return this
            }
            if (node!!.children.containsKey(part)) {
                node = node.children[part]
            } else {
                createNewBranch = true
                val tmp = Node()
                node.children[part] = tmp
                node = tmp
            }
        }
        // Turn the matching node into a leaf node (i.e., remove sub-paths).
        node!!.children.clear()
        return this
    }

    /** Merges all field paths in a FieldMask into this tree.  */
    @CanIgnoreReturnValue
    fun mergeFromFieldMask(mask: FieldMask): FieldMaskTree {
        for (path: String in mask.pathsList) {
            addFieldPath(path)
        }
        return this
    }

    /**
     * Removes `path` from the tree.
     *
     *
     * When removing a field path from the tree:
     *  * All sub-paths will be removed. That is, after removing "foo.bar" from the tree,
     * "foo.bar.baz" will be removed.
     *  * If all children of a node has been removed, the node itself will be removed as well.
     * That is, if "foo" only has one child "bar" and "foo.bar" only has one child "baz",
     * removing "foo.bar.barz" would remove both "foo" and "foo.bar".
     * If "foo" has both "bar" and "qux" as children, removing "foo.bar" would leave the path
     * "foo.qux" intact.
     *  * If the field path to remove is a non-exist sub-path, nothing will be changed.
     *
     */
    @CanIgnoreReturnValue
    fun removeFieldPath(path: String?): FieldMaskTree {
        val parts =
            Splitter.onPattern(FIELD_PATH_SEPARATOR_REGEX).splitToList(
                path!!
            )
        if (parts.isEmpty()) {
            return this
        }
        removeFieldPath(root, parts, 0)
        return this
    }

    /** Removes all field paths in `mask` from this tree.  */
    @CanIgnoreReturnValue
    fun removeFromFieldMask(mask: FieldMask): FieldMaskTree {
        for (path: String? in mask.pathsList) {
            removeFieldPath(path)
        }
        return this
    }

    /**
     * Converts this tree to a FieldMask.
     */
    fun toFieldMask(): FieldMask {
        if (root.children.isEmpty()) {
            return FieldMask.getDefaultInstance()
        }
        val paths = ArrayList<String>()
        getFieldPaths(root, "", paths)
        return FieldMask.newBuilder().addAllPaths(paths).build()
    }

    /**
     * Adds the intersection of this tree with the given `path` to `output`.
     */
    fun intersectFieldPath(path: String, output: FieldMaskTree) {
        if (root.children.isEmpty()) {
            return
        }
        val parts: Array<String> =
            path.split(FIELD_PATH_SEPARATOR_REGEX).toTypedArray()
        if (parts.isEmpty()) {
            return
        }
        var node: Node = root
        for (part: String? in parts) {
            if (node != root && node.children.isEmpty()) {
                // The given path is a sub-path of an existing leaf node in the tree.
                output.addFieldPath(path)
                return
            }
            val childNode = node.children[part]
            if (childNode != null) {
                node = childNode
            } else {
                return
            }
        }
        // We found a matching node for the path. All leaf children of this matching
        // node is in the intersection.
        val paths = ArrayList<String>()
        getFieldPaths(node, path, paths)
        for (value: String in paths) {
            output.addFieldPath(value)
        }
    }

    /**
     * Merges all fields specified by this FieldMaskTree from `source` to `destination`.
     */
    fun merge(
        source: Message,
        destination: Message.Builder,
        options: MergeOptions = DEFAULT_OPTION
    ) {
        if (source.descriptorForType != destination.descriptorForType) {
            throw IllegalArgumentException("Cannot merge messages of different types.")
        }
        if (root.children.isEmpty()) {
            return
        }
        merge(root, "", source, destination, options)
    }

    companion object {
        private val logger = Logger.getLogger(FieldMaskTree::class.java.name)
        private const val FIELD_PATH_SEPARATOR_REGEX = "\\."

        /**
         * Removes `parts` from `node` recursively.
         *
         * @return a boolean value indicating whether current `node` should be removed.
         */
        @CanIgnoreReturnValue
        private fun removeFieldPath(
            node: Node,
            parts: List<String>,
            index: Int
        ): Boolean {
            val key = parts[index]

            val childNode = node.children[key] ?: return false

            // Base case 1: path not match.
            // Base case 2: last element in parts.
            if (index == parts.size - 1) {
                node.children.remove(key)
                return node.children.isEmpty()
            }
            // Recursive remove sub-path.
            if (removeFieldPath(childNode, parts, index + 1)) {
                node.children.remove(key)
            }
            return node.children.isEmpty()
        }

        /** Gathers all field paths in a sub-tree.  */
        private fun getFieldPaths(
            node: Node,
            path: String,
            paths: MutableList<String>
        ) {
            if (node.children.isEmpty()) {
                paths.add(path)
                return
            }
            for (entry: Map.Entry<String, Node> in node.children.entries) {
                val childPath = if (path.isEmpty()) entry.key else path + "." + entry.key
                getFieldPaths(entry.value, childPath, paths)
            }
        }

        /** Merges all fields specified by a sub-tree from `source` to `destination`.  */
        private fun merge(
            node: Node,
            path: String,
            source: Message,
            destination: Message.Builder,
            options: MergeOptions
        ) {
            if (source.descriptorForType != destination.descriptorForType) {
                throw IllegalArgumentException(
                    String.format(
                        "source (%s) and destination (%s) descriptor must be equal",
                        source.descriptorForType, destination.descriptorForType
                    )
                )
            }
            val descriptor = source.descriptorForType
            for (entry: Map.Entry<String, Node> in node.children.entries) {
                val field = descriptor.findFieldByName(entry.key)
                if (field == null) {
                    logger.warning(
                        "Cannot find field \"" +
                            entry.key +
                            "\" in message type " +
                            descriptor.fullName
                    )
                    continue
                }
                if (!entry.value.children.isEmpty()) {
                    if (field.isRepeated || field.javaType != Descriptors.FieldDescriptor.JavaType.MESSAGE) {
                        logger.warning(
                            (
                                "Field \"" +
                                    field.fullName +
                                    "\" is not a " +
                                    "singular message field and cannot have sub-fields."
                                )
                        )
                        continue
                    }
                    if (!source.hasField(field) && !destination.hasField(field)) {
                        // If the message field is not present in both source and destination, skip recursing
                        // so we don't create unnecessary empty messages.
                        continue
                    }
                    val childPath = if (path.isEmpty()) entry.key else path + "." + entry.key
                    val childBuilder = (destination.getField(field) as Message).toBuilder()
                    merge(
                        entry.value,
                        childPath,
                        source.getField(field) as Message,
                        childBuilder,
                        options
                    )
                    destination.setField(field, childBuilder.buildPartial())
                    continue
                }
                if (field.isRepeated) {
                    if (options.replaceRepeatedFields()) {
                        destination.setField(field, source.getField(field))
                    } else {
                        for (element in source.getField(field) as List<*>) {
                            destination.addRepeatedField(field, element)
                        }
                    }
                } else {
                    if (field.javaType == Descriptors.FieldDescriptor.JavaType.MESSAGE) {
                        if (options.replaceMessageFields()) {
                            if (!source.hasField(field)) {
                                destination.clearField(field)
                            } else {
                                destination.setField(field, source.getField(field))
                            }
                        } else {
                            if (source.hasField(field)) {
                                destination.setField(
                                    field,
                                    (destination.getField(field) as Message)
                                        .toBuilder()
                                        .mergeFrom(source.getField(field) as Message)
                                        .build()
                                )
                            }
                        }
                    } else {
                        if (source.hasField(field) || !options.replacePrimitiveFields()) {
                            destination.setField(field, source.getField(field))
                        } else {
                            destination.clearField(field)
                        }
                    }
                }
            }
        }
    }

    /**
     * Check whether the field mask exist for the given path of the field number
     */
    fun isfieldMaskExist(source: Message, fieldNumberPath: List<Int>): Boolean {
        if (fieldNumberPath.isEmpty()) {
            return false
        }
        var children = root.children
        val iterator = fieldNumberPath.iterator()
        var descriptorForType = source.descriptorForType
        while (iterator.hasNext()) {
            val fieldDescriptor =
                descriptorForType.findFieldByNumber(iterator.next()) ?: return false
            val childNode = children[fieldDescriptor.toProto().name] ?: return false
            val field = source.getField(fieldDescriptor)
            when {
                fieldDescriptor.isRepeated -> {
                    return !iterator.hasNext()
                }
                fieldDescriptor.javaType == Descriptors.FieldDescriptor.JavaType.MESSAGE -> {
                    children = childNode.children
                    descriptorForType = (field as Message).descriptorForType
                }
                else -> {
                    return !iterator.hasNext()
                }
            }
        }
        return true
    }
}
