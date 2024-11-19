package com.example.unsplash.presentation.util

import com.example.unsplash.R
import com.studios1299.playwall.core.domain.error_handling.DataError

fun DataError.asStringResource(): Int {
    return when (this) {
        DataError.Network.REQUEST_TIMEOUT -> R.string.the_request_timed_out
        DataError.Network.TOO_MANY_REQUESTS -> R.string.youve_hit_your_rate_limit
        DataError.Network.NO_INTERNET -> R.string.no_internet
        DataError.Network.PAYLOAD_TOO_LARGE -> R.string.file_too_large
        DataError.Network.SERVER_ERROR -> R.string.server_error
        DataError.Network.SERIALIZATION -> R.string.error_serialization
        DataError.Network.UNKNOWN -> R.string.unknown_error
        DataError.Local.DISK_FULL -> R.string.error_disk_full
        DataError.Network.FORBIDDEN -> R.string.forbidden
        DataError.Network.NOT_FOUND -> R.string.not_found
        DataError.Network.UNAUTHORIZED -> R.string.unauthorized
        DataError.Network.CONFLICT -> R.string.conflict
    }
}