package com.weronka.golonka.exceptions

class UnexpectedError(message: String, cause: Throwable? = null) : RuntimeException(message, cause)