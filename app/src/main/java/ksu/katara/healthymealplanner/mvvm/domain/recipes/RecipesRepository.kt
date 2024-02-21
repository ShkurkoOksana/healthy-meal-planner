package ksu.katara.healthymealplanner.mvvm.domain.recipes

import ksu.katara.healthymealplanner.foundation.model.Repository
import ksu.katara.healthymealplanner.mvvm.domain.recipes.entities.Recipe
import ksu.katara.healthymealplanner.mvvm.domain.recipes.entities.RecipeDetails
import ksu.katara.healthymealplanner.mvvm.domain.recipes.entities.RecipeIngredient
import ksu.katara.healthymealplanner.mvvm.domain.recipes.entities.RecipePreparationStep

typealias RecipeDetailsListener = (recipeDetails: RecipeDetails) -> Unit
typealias RecipeIngredientsListener = (recipeIngredients: List<RecipeIngredient>) -> Unit
typealias RecipesInCategoryListener = (recipes: List<Recipe>) -> Unit

/**
 * Repository of recipes interface.
 *
 * Provides access to the available recipes.
 */
interface RecipesRepository : Repository {

    /**
     * Load the list of all available recipes in category with id that may be chosen by the user.
     */
    suspend fun loadRecipesInCategory(recipeCategoryId: Long): List<Recipe>

    /**
     * Listen for the current recipes in category changes.
     * The listener is triggered immediately with the current value when calling this method.
     */
    fun addRecipeInCategoryListener(listener: RecipesInCategoryListener)

    /**
     * Stop listening for the current recipes in category.
     */
    fun removeRecipeInCategoryListener(listener: RecipesInCategoryListener)

    /**
     * Get available recipes details.
     */

    suspend fun loadRecipeDetails(recipeId: Long): RecipeDetails

    /**
     * Listen for the current recipe details changes.
     * The listener is triggered immediately with the current value when calling this method.
     */
    fun addRecipeDetailsListener(listener: RecipeDetailsListener)

    /**
     * Stop listening for the recipe details.
     */
    fun removeRecipeDetailsListener(listener: RecipeDetailsListener)

    /**
     * Load the list of all available recipe types for recipe with id.
     */
    suspend fun loadRecipeTypes(recipeId: Long): List<String>

    /**
     * Load the list of all available recipe ingredients for recipe with id.
     */
    suspend fun loadIngredients(recipeId: Long): List<RecipeIngredient>

    /**
     * Listen for the current recipe ingredients changes.
     * The listener is triggered immediately with the current value when calling this method.
     */
    fun addIngredientListener(listener: RecipeIngredientsListener)

    /**
     * Stop listening for the recipe ingredients.
     */
    fun removeIngredientsListener(listener: RecipeIngredientsListener)

    /**
     * Set for recipe ingredient property isInShoppingList equals isSelected
     */
    suspend fun setIngredientSelected(recipeId: Long, ingredientId: Long, isSelected: Boolean)

    /**
     * Set for all ingredients for recipe with id property isInShoppingList equals isSelected
     */
    suspend fun setAllIngredientsSelected(recipeId: Long, isSelected: Boolean)

    /**
     * Determines if all ingredients are selected for recipe with id
     */
    suspend fun isAllIngredientsSelected(recipeId: Long): Boolean

    /**
     * Load the list of all available recipe preparation steps for recipe with id.
     */
    suspend fun loadPreparationSteps(recipeId: Long): List<RecipePreparationStep>

}