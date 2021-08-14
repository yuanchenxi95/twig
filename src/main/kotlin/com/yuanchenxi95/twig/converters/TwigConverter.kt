package com.yuanchenxi95.twig.converters

interface TwigConverter<T, R> {
    fun doForward(source: T): R

    fun doBackward(target: R): T
}
