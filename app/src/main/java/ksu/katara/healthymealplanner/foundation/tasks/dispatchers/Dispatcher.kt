package ksu.katara.healthymealplanner.foundation.tasks.dispatchers

/**
 * Dispatchers run the specified block of code in some way.
 */
interface Dispatcher {

    fun dispatch(block: () -> Unit)

}