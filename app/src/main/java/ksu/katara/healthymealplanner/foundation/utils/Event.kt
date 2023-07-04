package ksu.katara.healthymealplanner.foundation.utils

import androidx.lifecycle.LiveData

/**
 * Represents "side effect".
 * Used in [LiveData] as a wrapper for events.
 */

class Event<T>(
    value: T
) {

    private var _value: T? = value

    fun get(): T? = _value.also { _value = null }

}




