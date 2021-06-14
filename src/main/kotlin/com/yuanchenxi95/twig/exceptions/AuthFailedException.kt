package com.yuanchenxi95.twig.exceptions

import java.lang.RuntimeException

class AuthFailedException : RuntimeException {
    constructor() : super()
    constructor(message: String?) : super(message)
}
