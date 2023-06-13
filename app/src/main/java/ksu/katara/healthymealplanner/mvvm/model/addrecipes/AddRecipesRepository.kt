package ksu.katara.healthymealplanner.mvvm.model.addrecipes

import ksu.katara.healthymealplanner.foundation.model.Repository
import ksu.katara.healthymealplanner.mvvm.model.meal.MealTypes
import ksu.katara.healthymealplanner.mvvm.model.recipes.entities.Recipe
import ksu.katara.healthymealplanner.mvvm.tasks.Task

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
    fun loadAddRecipes(selectedDate: String, mealTypes: MealTypes): Task<Unit>

    /**
     * Listen for the current added recipes changes.
     * The listener is triggered immediately with the current value when calling this method.
     */
    fun addAddRecipesListener(listener: AddRecipesListener)

    /**
     * Stop listening for the current added recipe changes.
     */
    fun removeAddRecipesListener(listener: AddRecipesListener)

    /**
     * Delete recipe from  available added recipes.
     */
    fun addRecipesDeleteRecipe(recipe: Recipe): Task<Unit>

}