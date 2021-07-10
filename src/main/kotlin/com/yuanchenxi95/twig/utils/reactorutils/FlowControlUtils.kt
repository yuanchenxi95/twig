package com.yuanchenxi95.twig.utils.reactorutils

import org.reactivestreams.Publisher
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.function.Function

const val DEFAULT_LIMIT = 10

fun <T> parallelExecuteWithLimit(
    tasks: Iterable<Publisher<T>>,
    limit: Int = DEFAULT_LIMIT
): Flux<T> {
    return Flux.fromIterable(tasks)
        .flatMap(Function.identity(), limit)
}

fun <T> parallelExecuteWithLimitOrderedArray(
    tasks: Iterable<Mono<T>>,
    limit: Int = DEFAULT_LIMIT
): Mono<List<T>> {
    val indexedTasks = tasks
        .mapIndexed { index, observable ->
            observable.map { Pair(index, it) }
        }

    return parallelExecuteWithLimit(
        indexedTasks.asIterable(),
        limit
    )
        .collectSortedList { o1, o2 -> o1.first - o2.first }
        .map { resultPairs -> resultPairs.map { it.second } }
}
