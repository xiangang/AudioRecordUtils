package com.nxg.app.data

/**
 * A generic class that holds a value with its loading status.
 * 为了避免和Kotlin自带的Result冲突，这里重命名为KResult，K是Kotlin的意思
 * @param <T>
 */
sealed class KResult<out R> {

    data class Success<out T>(val data: T) : KResult<T>()
    data class Error(val exception: Exception) : KResult<Nothing>()
    object Loading : KResult<Nothing>()

    override fun toString(): String {
        return when (this) {
            is Success<*> -> "Success[data=$data]"
            is Error -> "Error[exception=$exception]"
            Loading -> "Loading"
        }
    }
}

/**
 * `true` if [KResult] is of type [Success] & holds non-null [Success.data].
 */
val KResult<*>.succeeded
    get() = this is KResult.Success && data != null
