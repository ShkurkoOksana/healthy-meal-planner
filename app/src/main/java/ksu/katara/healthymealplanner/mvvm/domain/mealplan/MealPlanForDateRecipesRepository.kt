package ksu.katara.healthymealplanner.mvvm.domain.mealplan

import ksu.katara.healthymealplanner.foundation.model.Repository
import ksu.katara.healthymealplanner.mvvm.domain.mealplan.entities.MealPlanRecipes
import ksu.katara.healthymealplanner.mvvm.domain.recipes.entities.Recipe
import ksu.katara.healthymealplanner.mvvm.presentation.main.tabs.home.MealTypes
import java.util.Date

typealias MealPlanForDateRecipesListener = (mealPlanRecipes: MealPlanRecipes?) -> Unit
typealias AddRecipesListener = (addRecipes: List<Recipe>) -> Unit

/**
 * Repository of meal plan recipes interface.
 *
 * Provides access to the available meal plan recipes.
 */
interface MealPlanForDateRecipesRepository : Repository {

    /**
     * Load the list of all available meal plan recipes that may be chosen by the user.
     */
    suspend fun loadMealPlanForDate(selectedDate: Date, mealType: MealTypes): MealPlanRecipes?

    /**
     * Listen for the current meal plan recipes changes.
     * The listener is triggered immediately with the current value when calling this method.
     */
    fun addMealPlanForDateListener(listener: MealPlanForDateRecipesListener)

    /**
     * Stop listening for the current meal plan recipes.
     */
    fun removeMealPlanForDateListener(listener: MealPlanForDateRecipesListener)

    /**
     * Add recipe to meal plan for selected date and meal type.
     */
    suspend fun addRecipeToMealPlanForDate(selectedDate: Date, mealType: MealTypes, recipe: Recipe)

    /**
     * Delete recipe from meal plan for selected date and meal type.
     */
    suspend fun deleteRecipeFromMealPlanForDate(
        selectedDate: Date,
        mealType: MealTypes,
        recipe: Recipe
    ): MealPlanRecipes?

    /**
     * Load the list of all added available recipes that may be chosen by the user.
     */
    suspend fun loadAddRecipes(selectedDate: Date, mealTypes: MealTypes): List<Recipe>

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
    suspend fun deleteRecipeFromAddRecipes(recipe: Recipe)
}