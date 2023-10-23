package ksu.katara.healthymealplanner.mvvm.model.dietTips

import ksu.katara.healthymealplanner.foundation.model.Repository
import ksu.katara.healthymealplanner.mvvm.model.dietTips.entities.DietTip
import ksu.katara.healthymealplanner.mvvm.model.dietTips.entities.DietTipDetails
import ksu.katara.healthymealplanner.mvvm.model.dietTips.entities.DietTipChapter
import ksu.katara.healthymealplanner.mvvm.model.dietTips.entities.DietTipDetailSteps

typealias DietTipChaptersListener = (dietTipChapters: List<DietTipChapter>) -> Unit
typealias DietTipsListener = (dietTips: List<DietTip>) -> Unit

/**
 * Repository of diet tips interface.
 *
 * Provides access to the available diet tips.
 */
interface DietTipsRepository : Repository {

    /**
     * Load the list of all available diet tips chapters that may be chosen by the user.
     */
    suspend fun loadChapters(): List<DietTipChapter>

    /**
     * Load the list of all available diet tips that may be chosen by the user.
     */
    suspend fun loadDietTipsByChapterId(id: Long): List<DietTip>

    fun getDietTipsByChapterId(id: Long): List<DietTip>

    fun loadDietTipDetailsById(id: Long): DietTipDetails

    /**
     * Listen for the current diet tips changes.
     * The listener is triggered immediately with the current value when calling this method.
     */
    fun addDietTipsListener(listener: DietTipsListener)

    /**
     * Stop listening for the current diet tips changes.
     */
    fun removeDietTipsListener(listener: DietTipsListener)

    /**
     * Load the list of all available diet tips details that may be chosen by the user.
     */
    suspend fun loadDietTipDetailStepsById(id: Long): List<DietTipDetailSteps>

    /**
     * Listen for the current diet tips chapters changes.
     * The listener is triggered immediately with the current value when calling this method.
     */
    fun addChaptersListener(listener: DietTipChaptersListener)

    /**
     * Stop listening for the current diet tips chapters changes.
     */
    fun removeChaptersListener(listener: DietTipChaptersListener)

}
