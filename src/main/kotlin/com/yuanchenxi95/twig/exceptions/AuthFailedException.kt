package com.yuanchenxi95.twig.exceptions

class AuthFailedException : RuntimeException {
    constructor() : super()
    constructor(message: String?) : super(message)
}
