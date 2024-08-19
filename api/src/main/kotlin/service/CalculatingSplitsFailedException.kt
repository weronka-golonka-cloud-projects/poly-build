package com.weronka.golonka.service

class CalculatingSplitsFailedException(
    message: String,
    cause: Throwable? = null,
) : RuntimeException(message, cause)
