package ksu.katara.healthymealplanner.mvvm.model.dietTips

import ksu.katara.healthymealplanner.foundation.model.Repository
import ksu.katara.healthymealplanner.mvvm.model.dietTips.entities.DietTip
import ksu.katara.healthymealplanner.mvvm.model.dietTips.entities.DietTipChapter
import ksu.katara.healthymealplanner.mvvm.views.main.tabs.home.diettips.ChapterDietTips
import ksu.katara.healthymealplanner.mvvm.views.main.tabs.home.diettips.DietTipDetailsSteps

typealias ChapterDietTipsListListener = (chapterDietTipsList: List<ChapterDietTips>) -> Unit
typealias DietTipChaptersListener = (dietTipChapters: List<DietTipChapter>) -> Unit
typealias DietTipsListener = (dietTips: List<DietTip>) -> Unit

/**
 * Repository of diet tips interface.
 *
 * Provides access to the available diet tips.
 */
interface DietTipsRepository : Repository {

    /**
     * Load the list of chapters and list of dietTips that contain in chapter .
     */
    suspend fun loadChapterDietTipsList(): List<ChapterDietTips>

    /**
     * Listen for the current chapter and diet tips changes.
     * The listener is triggered immediately with the current value when calling this method.
     */
    fun addChapterDietTipsListListener(listener: ChapterDietTipsListListener)

    /**
     * Stop listening for the current chapter and diet tips changes.
     */
    fun removeChapterDietTipsListListener(listener: ChapterDietTipsListListener)

    /**
     * Load the list of all available diet tips that may be chosen by the user.
     */
    suspend fun loadDietTipsByChapterId(id: Long): List<DietTip>

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
    suspend fun loadDietTipDetailsStepsById(id: Long): DietTipDetailsSteps

}
