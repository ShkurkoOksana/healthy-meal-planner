package ksu.katara.healthymealplanner.foundation.navigator

import android.os.Bundle

/**
 * Navigation for your application
 */
interface Navigator {

    /**
     * Launch a new screen at the top of back stack.
     */
    fun launch(destinationId: Int, args: Bundle?)

    /**
     * Go back to the previous screen and optionally send some results.
     */
    fun goBack(result: Any? = null)

}