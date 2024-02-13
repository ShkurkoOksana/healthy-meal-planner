package ksu.katara.healthymealplanner.mvvm.model.recipecategories

import ksu.katara.healthymealplanner.foundation.model.Repository
import ksu.katara.healthymealplanner.mvvm.model.recipecategories.entities.RecipeCategory

typealias RecipeCategoriesListener = (recipeCategories: List<RecipeCategory>) -> Unit

/**
 * Repository of recipe categories interface.
 *
 * Provides access to the available recipe categories.
 */
interface RecipeCategoriesRepository : Repository {

    /**
     * Load the list of all available recipe categories that may be chosen by the user.
     */
    suspend fun loadRecipeCategories(): List<RecipeCategory>

    /**
     * Get available recipe categories by id.
     */
    suspend fun getRecipeCategoryById(id: Long): RecipeCategory

    /**
     * Listen for the current recipe categories changes.
     * The listener is triggered immediately with the current value when calling this method.
     */
    fun addRecipeCategoriesListener(listener: RecipeCategoriesListener)

    /**
     * Stop listening for the current recipe categories.
     */
    fun removeRecipeCategoriesListener(listener: RecipeCategoriesListener)

}