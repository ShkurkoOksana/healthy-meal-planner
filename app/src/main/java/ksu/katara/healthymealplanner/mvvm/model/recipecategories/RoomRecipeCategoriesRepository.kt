package ksu.katara.healthymealplanner.mvvm.model.recipecategories

import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import ksu.katara.healthymealplanner.foundation.model.coroutines.IoDispatcher
import ksu.katara.healthymealplanner.mvvm.model.recipecategories.entities.RecipeCategory
import ksu.katara.healthymealplanner.mvvm.model.recipecategories.room.RecipeCategoriesDao

/**
 * Simple in-memory implementation of [RecipeCategoriesRepository]
 */
class RoomRecipeCategoriesRepository(
    private val recipeCategoriesDao: RecipeCategoriesDao,
    private val ioDispatcher: IoDispatcher
) : RecipeCategoriesRepository {

    private var categories = listOf<RecipeCategory>()
    private var loaded = false
    private val listeners = mutableSetOf<RecipeCategoriesListener>()

    override suspend fun loadRecipeCategories(): List<RecipeCategory> =
        withContext(ioDispatcher.value) {
            delay(1000L)
            categories = recipeCategoriesDao.loadRecipeCategories().map { it.toRecipeCategory() }
            loaded = true
            notifyChanges()
            return@withContext categories
        }

    override suspend fun getRecipeCategoryById(id: Long): RecipeCategory =
        withContext(ioDispatcher.value) {
            delay(1000L)
            val recipeCategory = recipeCategoriesDao.getRecipeCategoryById(id).toRecipeCategory()
            loaded = true
            notifyChanges()
            return@withContext recipeCategory
        }

    override fun addRecipeCategoriesListener(listener: RecipeCategoriesListener) {
        listeners.add(listener)
        if (loaded) {
            listener.invoke(categories)
        }
    }

    override fun removeRecipeCategoriesListener(listener: RecipeCategoriesListener) {
        listeners.remove(listener)
    }

    private fun notifyChanges() {
        if (!loaded) return
        listeners.forEach { it.invoke(categories) }
    }

}