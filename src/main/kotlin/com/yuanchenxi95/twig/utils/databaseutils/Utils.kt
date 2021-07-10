package com.yuanchenxi95.twig.utils.databaseutils

/**
 * Computes the diff between left set and right set
 * e.g. leftSet: {a, b, c}, rightSet: {b, c, d}. -> Pair({a}, {d})
 * @param leftSet the existing references.
 * @param rightSet the updated references.
 * @return the pair of the diff.
 */
fun computeDiff(
    leftSet: Set<String>,
    rightSet: Set<String>,
): Pair<Set<String>, Set<String>> {
    val intersectionSet = HashSet(leftSet)
    intersectionSet.retainAll(rightSet)
    return Pair(leftSet.minus(intersectionSet), rightSet.minus(intersectionSet))
}

fun <T> concatList(vararg itemsList: List<T>): List<T> {
    val result = ArrayList<T>()
    for (items in itemsList) {
        result.addAll(items)
    }
    return result
}
