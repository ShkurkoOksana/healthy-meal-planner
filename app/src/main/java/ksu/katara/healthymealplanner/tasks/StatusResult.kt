package ksu.katara.healthymealplanner.tasks

sealed class StatusResult<T> {

    @Suppress("UNCHECKED_CAST")
    fun <R> resultMap(mapper: (T) -> R): StatusResult<R> {
        if (this is SuccessResult) return SuccessResult(mapper(data))
        return this as StatusResult<R>
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

