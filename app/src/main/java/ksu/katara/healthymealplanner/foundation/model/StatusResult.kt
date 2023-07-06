package ksu.katara.healthymealplanner.foundation.model

typealias Mapper<Input, Output> = (Input) -> Output

sealed class StatusResult<T> {

    fun <R> resultMap(mapper: Mapper<T, R>? = null): StatusResult<R> = when(this) {
        is EmptyResult -> EmptyResult()
        is PendingResult -> PendingResult()
        is ErrorResult -> ErrorResult(this.error)
        is SuccessResult -> {
            if (mapper == null) throw IllegalArgumentException("Mapper should not be null for success result")
            SuccessResult(mapper(this.data))
        }
    }

}

class SuccessResult<T>(
    val data: T
) : StatusResult<T>()

class ErrorResult<T>(
    val error: Throwable
) : StatusResult<T>()

class PendingResult<T> : StatusResult<T>()

class EmptyResult<T> : StatusResult<T>()

fun <T> StatusResult<T>?.takeSuccess(): T? {
    return if (this is SuccessResult)
        this.data
    else
        null
}