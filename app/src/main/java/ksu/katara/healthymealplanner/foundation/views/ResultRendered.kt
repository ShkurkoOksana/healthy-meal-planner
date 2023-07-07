package ksu.katara.healthymealplanner.foundation.views

import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import ksu.katara.healthymealplanner.R
import ksu.katara.healthymealplanner.databinding.PartResultBinding
import ksu.katara.healthymealplanner.foundation.model.EmptyResult
import ksu.katara.healthymealplanner.foundation.model.ErrorResult
import ksu.katara.healthymealplanner.foundation.model.PendingResult
import ksu.katara.healthymealplanner.foundation.model.StatusResult
import ksu.katara.healthymealplanner.foundation.model.SuccessResult

/**
 * Default [Result] rendering.
 * - if [result] is [EmptyResult] -> only no data-bar is displayed
 * - if [result] is [PendingResult] -> only progress-bar is displayed
 * - if [result] is [ErrorResult] -> only error container is displayed
 * - if [result] is [SuccessResult] -> error container & progress-bar is hidden, all other views are visible
 */
fun <T> BaseFragment.renderSimpleResult(root: ViewGroup, result: StatusResult<T>, onEmpty: () -> Unit = {}, onSuccess: (T) -> Unit) {
    val binding = PartResultBinding.bind(root)
    renderResult(
        root = root,
        result = result,
        onEmpty = {
            binding.noData.visibility = View.VISIBLE
            onEmpty()
        },
        onPending = {
            binding.progressBar.visibility = View.VISIBLE
        },
        onError = {
            binding.errorContainer.visibility = View.VISIBLE
        },
        onSuccess = { successData ->
            root.children
                .filter { it.id != R.id.progressBar && it.id != R.id.errorContainer && it.id != R.id.noData}
                .forEach { it.visibility = View.VISIBLE }
            onSuccess(successData)
        }
    )
}