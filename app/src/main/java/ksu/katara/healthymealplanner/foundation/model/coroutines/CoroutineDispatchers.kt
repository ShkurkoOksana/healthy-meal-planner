package ksu.katara.healthymealplanner.foundation.model.coroutines

import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Holder for coroutine dispatcher which should be used for IO-intensive operations
 */
@Singleton
class IoDispatcher @Inject constructor(
    val value: CoroutineDispatcher
)