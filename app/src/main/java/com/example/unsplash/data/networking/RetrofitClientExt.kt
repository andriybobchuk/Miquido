package com.example.unsplash.data.networking

import android.util.Log
import com.studios1299.playwall.core.domain.error_handling.DataError
import com.studios1299.playwall.core.domain.error_handling.SmartResult
import retrofit2.Response
import java.nio.channels.UnresolvedAddressException
import kotlin.coroutines.cancellation.CancellationException


object RetrofitClientExt {

    const val LOG_TAG = "RetrofitClientExt"

    inline fun <reified T> safeCall(call: () -> Response<T>): SmartResult<T, DataError.Network> {
        val response = try {
            call()
        } catch(e: UnresolvedAddressException) {
            e.printStackTrace()
            Log.d(LOG_TAG, "safeCall(), Unresolved address(no internet): $e")
            return SmartResult.Error(DataError.Network.NO_INTERNET)
        } catch(e: Exception) {
            if(e is CancellationException) throw e
            e.printStackTrace()
            Log.d(LOG_TAG, "safeCall(), unknown exception: $e")
            return SmartResult.Error(DataError.Network.UNKNOWN)
        }
        return responseToResult(response)
    }

    inline fun <reified T> responseToResult(response: Response<T>): SmartResult<T, DataError.Network> {
        return if (response.isSuccessful) {
            Log.d(LOG_TAG, "responseToResult(), success: ${response.body().toString()}")
            SmartResult.Success(response.body()!!)
        } else {
            Log.d(LOG_TAG, "responseToResult(), error: ${response.errorBody().toString()}")
            when(response.code()) {
                401 -> SmartResult.Error(DataError.Network.UNAUTHORIZED)
                408 -> SmartResult.Error(DataError.Network.REQUEST_TIMEOUT)
                409 -> SmartResult.Error(DataError.Network.CONFLICT)
                413 -> SmartResult.Error(DataError.Network.PAYLOAD_TOO_LARGE)
                429 -> SmartResult.Error(DataError.Network.TOO_MANY_REQUESTS)
                in 500..599 -> SmartResult.Error(DataError.Network.SERVER_ERROR)
                else -> SmartResult.Error(DataError.Network.UNKNOWN)
            }
        }
    }
}
