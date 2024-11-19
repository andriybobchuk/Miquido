package com.studios1299.playwall.core.domain.error_handling

/**
 * I can argue about this error handling class for hours actually, its far from ideal so Id be
 * happy to get some feedback :)
 */
typealias RootError = Error

sealed interface SmartResult<out D, out E: RootError> {
    data class Success<out D>(val data: D): SmartResult<D, Nothing>
    data class Error<out E: RootError>(val error: E): SmartResult<Nothing, E>
}

inline fun <D> SmartResult<D, *>.onSuccess(action: (D) -> Unit): SmartResult<D, *> {
    if (this is SmartResult.Success) {
        action(data)
    }
    return this
}

inline fun <E: RootError> SmartResult<*, E>.onError(action: (E) -> Unit): SmartResult<*, E> {
    if (this is SmartResult.Error) {
        action(error)
    }
    return this
}

// Im proud of this function tbh, especially the way it works with RetrofitClientExt.safeCall()
inline fun <D, E: RootError, R> SmartResult<D, E>.fold(
    onSuccess: (D) -> R,
    onError: (E) -> R
): R = when (this) {
    is SmartResult.Success -> onSuccess(this.data)
    is SmartResult.Error -> onError(this.error)
}