package ksu.katara.healthymealplanner.foundation.model.dispatchers

class ImmediateDispatcher : Dispatcher {
    override fun dispatch(block: () -> Unit) {
        block()
    }
}