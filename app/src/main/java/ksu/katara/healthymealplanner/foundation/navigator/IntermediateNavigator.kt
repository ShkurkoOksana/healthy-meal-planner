package ksu.katara.healthymealplanner.foundation.navigator

import android.os.Bundle
import ksu.katara.healthymealplanner.foundation.utils.ResourceActions

/**
 * Mediator that holds nav actions in the queue if real navigator is not active.
 */
class IntermediateNavigator : Navigator {

    private val targetNavigator = ResourceActions<Navigator>()

    override fun launch(destinationId: Int, args: Bundle?) = targetNavigator {
        it.launch(destinationId, args)
    }

    override fun goBack(result: Any?) = targetNavigator {
        it.goBack(result)
    }

    fun setTarget(navigator: Navigator?) {
        targetNavigator.resource = navigator
    }

    fun clear() {
        targetNavigator.clear()
    }
}