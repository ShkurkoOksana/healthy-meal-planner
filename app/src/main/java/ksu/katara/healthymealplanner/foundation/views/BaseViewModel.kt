package ksu.katara.healthymealplanner.foundation.views

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ksu.katara.healthymealplanner.foundation.model.PendingResult
import ksu.katara.healthymealplanner.foundation.model.StatusResult
import ksu.katara.healthymealplanner.foundation.tasks.Task
import ksu.katara.healthymealplanner.foundation.tasks.TaskListener
import ksu.katara.healthymealplanner.foundation.tasks.dispatchers.Dispatcher

typealias LiveResult<T> = LiveData<StatusResult<T>>
typealias MutableLiveResult<T> = MutableLiveData<StatusResult<T>>

/**
 * Base class for all view-models.
 */
open class BaseViewModel(
    private val dispatcher: Dispatcher
) : ViewModel() {

    /**
     * Override this method in child classes if you want to listen for results
     * from other screens
     */
    open fun onResult(result: Any) {

    }

    fun onBackPressed() {
        clearTasks()
    }

    private val tasks = mutableListOf<Task<*>>()

    override fun onCleared() {
        clearTasks()
    }

    /**
     * Launch task asynchronously, listen for its result and
     * automatically unsubscribe the listener in case of view-model destroying.
     */
    fun <T> Task<T>.safeEnqueue(listener: TaskListener<T>? = null) {
        tasks.add(this)
        this.enqueue(dispatcher) {
            tasks.remove(this)
            listener?.invoke(it)
        }
    }

    /**
     * Launch task asynchronously and map its result to the specified
     * [liveResult].
     * Task is cancelled automatically if view-model is going to be destroyed.
     */
    fun <T> Task<T>.into(liveResult: MutableLiveResult<T>) {
        liveResult.value = PendingResult()
        this.safeEnqueue {
            liveResult.value = it
        }
    }

    private fun clearTasks() {
        tasks.forEach { it.cancel() }
        tasks.clear()
    }
}