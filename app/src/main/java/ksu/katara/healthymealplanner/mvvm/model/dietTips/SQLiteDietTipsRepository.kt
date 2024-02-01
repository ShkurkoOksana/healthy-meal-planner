package ksu.katara.healthymealplanner.mvvm.model.dietTips

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import ksu.katara.healthymealplanner.foundation.model.coroutines.IoDispatcher
import ksu.katara.healthymealplanner.mvvm.model.dietTips.entities.DietTip
import ksu.katara.healthymealplanner.mvvm.model.dietTips.entities.DietTipChapter
import ksu.katara.healthymealplanner.mvvm.model.dietTips.entities.DietTipDetailSteps
import ksu.katara.healthymealplanner.mvvm.model.dietTips.entities.DietTipDetails
import ksu.katara.healthymealplanner.mvvm.model.sqlite.AppSQLiteContract.DietTipChaptersTable
import ksu.katara.healthymealplanner.mvvm.model.sqlite.AppSQLiteContract.DietTipDetailStepsTable
import ksu.katara.healthymealplanner.mvvm.model.sqlite.AppSQLiteContract.DietTipDetailsTable
import ksu.katara.healthymealplanner.mvvm.model.sqlite.AppSQLiteContract.DietTipsTable

class SQLiteDietTipsRepository(
    private val db: SQLiteDatabase,
    private val ioDispatcher: IoDispatcher
) : DietTipsRepository {

    private lateinit var chapters: List<DietTipChapter>
    private var chaptersLoaded = false
    private val chapterListeners = mutableSetOf<DietTipChaptersListener>()

    private lateinit var dietTips: List<DietTip>
    private var dietTipsLoaded = false
    private val dietTipListeners = mutableSetOf<DietTipsListener>()

    override suspend fun loadChapters(): List<DietTipChapter> = withContext(ioDispatcher.value) {
        delay(1000L)
        chapters = getChapters()
        chaptersLoaded = true
        notifyChaptersChanges()
        return@withContext chapters
    }

    override suspend fun loadDietTipsByChapterId(id: Long): List<DietTip> =
        withContext(ioDispatcher.value) {
            delay(1000L)
            dietTips = getDietTipsByChapterId(id)
            dietTipsLoaded = true
            notifyDietTipsChanges()
            return@withContext dietTips
        }

    override fun getDietTipsByChapterId(id: Long): List<DietTip> {
        val cursor = queryDietTipsByChapterId(id)
        return cursor.use {
            val list = mutableListOf<DietTip>()
            while (cursor.moveToNext()) {
                list.add(parseDietTip(cursor))
            }
            return@use list
        }
    }

    override fun loadDietTipDetailsById(id: Long): DietTipDetails {
        return getDietTipDetailsById(id)
    }

    override suspend fun loadDietTipDetailStepsById(id: Long): List<DietTipDetailSteps> =
        withContext(ioDispatcher.value) {
            delay(1000L)
            return@withContext getStepsByDietTipDetailsId(id)
        }

    private fun getChapters(): List<DietTipChapter> {
        val cursor = queryChapters()
        return queryChapters().use {
            val list = mutableListOf<DietTipChapter>()
            while (cursor.moveToNext()) {
                list.add(parseChapter(cursor))
            }
            return@use list
        }
    }

    private fun queryChapters() =
        db.rawQuery("SELECT * FROM ${DietTipChaptersTable.TABLE_NAME}", null)

    private fun parseChapter(cursor: Cursor): DietTipChapter {
        val id = cursor.getLong(cursor.getColumnIndexOrThrow(DietTipChaptersTable.COLUMN_ID))
        return DietTipChapter(
            id = id,
            name = cursor.getString(cursor.getColumnIndexOrThrow(DietTipChaptersTable.COLUMN_NAME)),
        )
    }

    private fun queryDietTipsByChapterId(id: Long): Cursor {
        return db.rawQuery(
            "SELECT * FROM ${DietTipsTable.TABLE_NAME} " +
                    "WHERE ${DietTipsTable.COLUMN_CHAPTER_ID}=?",
            arrayOf(id.toString()))
    }

    private fun parseDietTip(cursor: Cursor): DietTip {
        return DietTip(
            id = cursor.getLong(cursor.getColumnIndexOrThrow(DietTipsTable.COLUMN_ID)),
            photo = cursor.getString(cursor.getColumnIndexOrThrow(DietTipsTable.COLUMN_PHOTO)),
            name = cursor.getString(cursor.getColumnIndexOrThrow(DietTipsTable.COLUMN_NAME)),
            dietTipDetailsId = cursor.getLong(cursor.getColumnIndexOrThrow(DietTipsTable.COLUMN_DIET_TIP_DETAILS_ID)),
            chapterId = cursor.getLong(cursor.getColumnIndexOrThrow(DietTipsTable.COLUMN_CHAPTER_ID)),
        )
    }

    private fun getDietTipDetailsById(id: Long): DietTipDetails {
        val cursor = queryDietTipDetailsById(id)
        cursor.use {
            cursor.moveToFirst()
            return parseDietTipDetail(cursor)
        }
    }

    private fun queryDietTipDetailsById(id: Long): Cursor {
        return db.rawQuery(
            "SELECT * FROM ${DietTipDetailsTable.TABLE_NAME} " +
                    "WHERE ${DietTipDetailsTable.COLUMN_ID}=?",
            arrayOf(id.toString()))
    }

    private fun parseDietTipDetail(cursor: Cursor): DietTipDetails {
        return DietTipDetails(
            id = cursor.getLong(cursor.getColumnIndexOrThrow(DietTipDetailsTable.COLUMN_ID)),
            background = cursor.getString(cursor.getColumnIndexOrThrow(DietTipDetailsTable.COLUMN_BACKGROUND)),
        )
    }

    private fun getStepsByDietTipDetailsId(id: Long): List<DietTipDetailSteps> {
        val cursor = queryStepsByDietTipDetailsId(id)
        return cursor.use {
            val list = mutableListOf<DietTipDetailSteps>()
            while (cursor.moveToNext()) {
                list.add(parseStep(cursor))
            }
            return@use list
        }
    }

    private fun queryStepsByDietTipDetailsId(id: Long): Cursor {
        return db.rawQuery(
            "SELECT * FROM ${DietTipDetailStepsTable.TABLE_NAME} " +
                    "WHERE ${DietTipDetailStepsTable.COLUMN_DIET_TIP_DETAILS_ID}=?",
            arrayOf(id.toString())
        )
    }

    private fun parseStep(cursor: Cursor): DietTipDetailSteps {
        return DietTipDetailSteps(
            id = cursor.getLong(cursor.getColumnIndexOrThrow(DietTipDetailStepsTable.COLUMN_ID)),
            indexNumber = cursor.getInt(cursor.getColumnIndexOrThrow(DietTipDetailStepsTable.COLUMN_INDEX_NUMBER)),
            title = cursor.getString(cursor.getColumnIndexOrThrow(DietTipDetailStepsTable.COLUMN_TITLE_NAME)),
            description = cursor.getString(cursor.getColumnIndexOrThrow(DietTipDetailStepsTable.COLUMN_TITLE_DESCRIPTION)),
            dietTipDetailId = cursor.getLong(cursor.getColumnIndexOrThrow(DietTipDetailStepsTable.COLUMN_DIET_TIP_DETAILS_ID))
        )
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

    override fun addChaptersListener(listener: DietTipChaptersListener) {
        chapterListeners.add(listener)
        if (chaptersLoaded) {
            listener.invoke(chapters)
        }
    }

    override fun removeChaptersListener(listener: DietTipChaptersListener) {
        chapterListeners.remove(listener)
    }

    private fun notifyChaptersChanges() {
        if (!chaptersLoaded) return
        chapterListeners.forEach { it.invoke(chapters) }
    }
}
