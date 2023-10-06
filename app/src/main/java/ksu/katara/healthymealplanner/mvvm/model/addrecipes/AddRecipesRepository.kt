package ksu.katara.healthymealplanner.mvvm.model.addrecipes

import ksu.katara.healthymealplanner.foundation.model.Repository
import ksu.katara.healthymealplanner.mvvm.model.recipes.entities.Recipe
import ksu.katara.healthymealplanner.mvvm.views.main.tabs.home.MealTypes
import java.util.Date

typealias AddRecipesListener = (addRecipes: List<Recipe>) -> Unit

/**
 * Repository of added recipes interface.
 *
 * Provides access to the available added recipes.
 */
interface AddRecipesRepository : Repository {

    /**
     * Load the list of all added available recipes that may be chosen by the user.
     */
    suspend fun load(selectedDate: Date, mealTypes: MealTypes): List<Recipe>

    /**
     * Listen for the current added recipes changes.
     * The listener is triggered immediately with the current value when calling this method.
     */
    fun addListener(listener: AddRecipesListener)

    /**
     * Stop listening for the current added recipe changes.
     */
    fun removeListener(listener: AddRecipesListener)

    /**
     * Delete recipe from  available added recipes.
     */
    suspend fun deleteRecipe(recipe: Recipe)

}