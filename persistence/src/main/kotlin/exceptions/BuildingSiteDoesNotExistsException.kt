package com.weronka.golonka.exceptions

class BuildingSiteDoesNotExistsException(
    message: String, cause: Throwable? = null
) : RuntimeException(message, cause)