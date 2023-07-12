package ksu.katara.healthymealplanner.foundation.model

typealias Mapper<Input, Output> = (Input) -> Output

/**
 * Base class which represents result of some async operation
 */
sealed class StatusResult<T> {

    /**
     * Convert this result of type T into another result of type R:
     * - error result of type T is converted to error result of type R with the same exception
     * - pending result of type T is converted to pending result of type R
     * - success result of type T is converted to success result of type R, where conversion
     *   of ([SuccessResult.data] from T to R is conducted by [mapper]
     */
    fun <R> resultMap(mapper: Mapper<T, R>? = null): StatusResult<R> = when(this) {
        is EmptyResult -> EmptyResult()
        is PendingResult -> PendingResult()
        is ErrorResult -> ErrorResult(this.exception)
        is SuccessResult -> {
            if (mapper == null) throw IllegalArgumentException("Mapper should not be NULL for success result")
            SuccessResult(mapper(this.data))
        }
    }

}

/**
 * Operation has been finished
 */
sealed class FinalResult<T> : StatusResult<T>()

/**
 * Operation has been empty
 */
class EmptyResult<T> : StatusResult<T>()

/**
 * Operation is in progress
 */
class PendingResult<T> : StatusResult<T>()

/**
 * Operation has finished successfully
 */
class SuccessResult<T>(
    val data: T
) : FinalResult<T>()

/**
 * Operation has finished with error
 */
class ErrorResult<T>(
    val exception: Throwable
) : FinalResult<T>()