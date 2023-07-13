package ksu.katara.healthymealplanner.foundation.views

import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import androidx.fragment.app.Fragment
import ksu.katara.healthymealplanner.foundation.model.EmptyResult
import ksu.katara.healthymealplanner.foundation.model.ErrorResult
import ksu.katara.healthymealplanner.foundation.model.PendingResult
import ksu.katara.healthymealplanner.foundation.model.StatusResult
import ksu.katara.healthymealplanner.foundation.model.SuccessResult


/**
 * Base class for all fragments
 */
abstract class BaseFragment : Fragment() {

    /**
     * View-model that manages this fragment
     */
    abstract val viewModel: BaseViewModel

    /**
     * Call this method when activity controls (e.g. toolbar) should be re-rendered
     */
    fun notifyScreenUpdates() {
        (requireActivity() as FragmentsHolder).notifyScreenUpdates()
    }

    /**
     * Hide all views in the [root] and then call one of the provided lambda functions
     * depending on [result]:
     * - [onEmpty] is called when [result] is [EmptyResult]
     * - [onPending] is called when [result] is [PendingResult]
     * - [onSuccess] is called when [result] is [SuccessResult]
     * - [onError] is called when [result] is [ErrorResult]
     */
    fun <T> renderResult(root: ViewGroup, result: StatusResult<T>,
                         onEmpty: () -> Unit,
                         onPending: () -> Unit,
                         onError: (Exception) -> Unit,
                         onSuccess: (T) -> Unit) {

        root.children.forEach { it.visibility = View.GONE }
        when (result) {
            is SuccessResult -> onSuccess(result.data)
            is ErrorResult -> onError(result.exception)
            is PendingResult -> onPending()
            is EmptyResult -> onEmpty()
        }

    }
}