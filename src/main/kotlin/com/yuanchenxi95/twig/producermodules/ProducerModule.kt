package com.yuanchenxi95.twig.producermodules

import reactor.core.publisher.Mono

interface ProducerModule<R> {
    interface ProducerModuleExecutor<R> {
        fun execute(): Mono<R>
    }
}
