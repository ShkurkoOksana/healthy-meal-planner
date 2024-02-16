package ksu.katara.healthymealplanner.mvvm.model.dietTips

import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import ksu.katara.healthymealplanner.foundation.model.coroutines.IoDispatcher
import ksu.katara.healthymealplanner.mvvm.model.dietTips.entities.DietTip
import ksu.katara.healthymealplanner.mvvm.model.dietTips.entities.DietTipChapter
import ksu.katara.healthymealplanner.mvvm.model.dietTips.room.DietTipsDao
import ksu.katara.healthymealplanner.mvvm.views.main.tabs.home.diettips.ChapterDietTips
import ksu.katara.healthymealplanner.mvvm.views.main.tabs.home.diettips.DietTipDetailsSteps
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RoomDietTipsRepository @Inject constructor(
    private val dietTipsDao: DietTipsDao,
    private val ioDispatcher: IoDispatcher
) : DietTipsRepository {

    private lateinit var chapterDietTipsList: List<ChapterDietTips>
    private var chapterDietTipsListLoaded = false
    private val chapterDietTipsListListeners = mutableSetOf<ChapterDietTipsListListener>()

    private lateinit var dietTips: List<DietTip>
    private var dietTipsLoaded = false
    private val dietTipListeners = mutableSetOf<DietTipsListener>()

    override suspend fun loadChapterDietTipsList(): List<ChapterDietTips> =
        withContext(ioDispatcher.value) {
            delay(1000L)
            val chapters = dietTipsDao.findChapters().map { it.toDietTipChapter() }
            val chapterDietTipsList: MutableList<ChapterDietTips> = mutableListOf()
            chapters.forEach { chapter: DietTipChapter ->
                val dietTips = dietTipsDao.findByChapterId(chapter.id).map { it.toDietTip() }
                chapterDietTipsList.add(ChapterDietTips(chapter, dietTips))
            }
            dietTipsLoaded = true
            notifyDietTipsChanges()
            chapterDietTipsListLoaded = true
            notifyChapterDietTipsListChanges()
            return@withContext chapterDietTipsList
        }

    override suspend fun loadDietTipsByChapterId(id: Long): List<DietTip> =
        withContext(ioDispatcher.value) {
            delay(1000L)
            val dietTips = dietTipsDao.findByChapterId(id).map { it.toDietTip() }
            dietTipsLoaded = true
            notifyDietTipsChanges()
            return@withContext dietTips
        }

    override suspend fun loadDietTipDetailsStepsById(id: Long): DietTipDetailsSteps =
        withContext(ioDispatcher.value) {
            delay(1000L)
            val dietTipDetails = dietTipsDao.findDietTipDetailsById(id).toDietTipDetails()
            val dietTipDetailSteps = dietTipsDao.findDietTipDetailStepsById(dietTipDetails.id)
                .map { it.toDietTipDetailSteps() }
            return@withContext DietTipDetailsSteps(dietTipDetails, dietTipDetailSteps)
        }

    override fun addChapterDietTipsListListener(listener: ChapterDietTipsListListener) {
        chapterDietTipsListListeners.add(listener)
        if (chapterDietTipsListLoaded) {
            listener.invoke(chapterDietTipsList)
        }
    }

    override fun removeChapterDietTipsListListener(listener: ChapterDietTipsListListener) {
        chapterDietTipsListListeners.remove(listener)
    }

    private fun notifyChapterDietTipsListChanges() {
        if (!chapterDietTipsListLoaded) return
        chapterDietTipsListListeners.forEach { it.invoke(chapterDietTipsList) }
    }

    override fun addDietTipsListener(listener: DietTipsListener) {
        dietTipListeners.add(listener)
        if (dietTipsLoaded) {
            listener.invoke(dietTips)
        }
    }

    override fun removeDietTipsListener(listener: DietTipsListener) {
        dietTipListeners.remove(listener)
    }

    private fun notifyDietTipsChanges() {
        if (!dietTipsLoaded) return
        dietTipListeners.forEach { it.invoke(dietTips) }
    }

}
