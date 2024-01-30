package ksu.katara.healthymealplanner.mvvm.model.recipecategories

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import ksu.katara.healthymealplanner.foundation.model.coroutines.IoDispatcher
import ksu.katara.healthymealplanner.mvvm.model.RecipeCategoryNotFoundException
import ksu.katara.healthymealplanner.mvvm.model.recipecategories.entities.Category
import ksu.katara.healthymealplanner.mvvm.model.sqlite.AppSQLiteContract.RecipeCategoriesTable

/**
 * Simple in-memory implementation of [CategoriesRepository]
 */
class SQLiteRecipeCategoriesRepository(
    private val db: SQLiteDatabase,
    private val ioDispatcher: IoDispatcher
) : CategoriesRepository {

    private var categories = listOf<Category>()
    private var loaded = false
    private val listeners = mutableSetOf<RecipeCategoriesListener>()

    override suspend fun load(): List<Category> = withContext(ioDispatcher.value) {
        delay(1000L)
        categories = findCategories()
        loaded = true
        notifyChanges()
        return@withContext categories
    }

    private fun findCategories(): List<Category> {
        val cursor = queryCategories()
        return cursor.use {
            val list = mutableListOf<Category>()
            while (cursor.moveToNext()) {
                list.add(parseCategories(cursor))
            }
            return@use list
        }
    }

    private fun parseCategories(cursor: Cursor): Category {
        return Category(
            id = cursor.getLong(cursor.getColumnIndexOrThrow(RecipeCategoriesTable.COLUMN_ID)),
            photo = cursor.getString(cursor.getColumnIndexOrThrow(RecipeCategoriesTable.COLUMN_PHOTO)),
            name = cursor.getString(cursor.getColumnIndexOrThrow(RecipeCategoriesTable.COLUMN_NAME))
        )
    }

    private fun queryCategories(): Cursor {
        val sql = "SELECT * FROM ${RecipeCategoriesTable.TABLE_NAME}"
        return db.rawQuery(sql, null)
    }

    override suspend fun getById(id: Long): Category = withContext(ioDispatcher.value) {
        delay(1000L)
        return@withContext categories.firstOrNull { it.id == id }
            ?: throw RecipeCategoryNotFoundException()
    }

    override fun addListener(listener: RecipeCategoriesListener) {
        listeners.add(listener)
        if (loaded) {
            listener.invoke(categories)
        }
    }

    override fun removeListener(listener: RecipeCategoriesListener) {
        listeners.remove(listener)
    }

    private fun notifyChanges() {
        if (!loaded) return
        listeners.forEach { it.invoke(categories) }
    }

}