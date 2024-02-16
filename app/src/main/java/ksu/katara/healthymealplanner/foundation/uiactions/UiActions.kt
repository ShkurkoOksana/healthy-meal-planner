package ksu.katara.healthymealplanner.foundation.uiactions

import javax.inject.Singleton

/**
 * Common actions that can be performed in the view-model
 */
@Singleton
interface UiActions {

    /**
     * Display a simple toast message.
     */
    fun toast(message: String)

    /**
     * Get string resource content by its identifier.
     */
    fun getString(messageRes: Int, vararg args: Any): String

}