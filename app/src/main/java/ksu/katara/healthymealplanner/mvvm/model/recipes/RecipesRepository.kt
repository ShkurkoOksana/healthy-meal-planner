package ksu.katara.healthymealplanner.mvvm.model.recipes

import ksu.katara.healthymealplanner.foundation.model.Repository
import ksu.katara.healthymealplanner.foundation.tasks.Task
import ksu.katara.healthymealplanner.mvvm.model.recipes.entities.Recipe
import ksu.katara.healthymealplanner.mvvm.model.recipes.entities.RecipeDetails
import ksu.katara.healthymealplanner.mvvm.model.recipes.entities.RecipeIngredient
import ksu.katara.healthymealplanner.mvvm.model.recipes.entities.RecipePreparationStep

typealias RecipeDetailsListener = (recipeDetails: List<RecipeDetails>) -> Unit
typealias RecipeIngredientsListener = (recipeIngredients: List<RecipeIngredient>) -> Unit
typealias RecipesInCategoryListener = (recipes: List<Recipe>) -> Unit

/**
 * Repository of recipes interface.
 *
 * Provides access to the available recipes.
 */
interface RecipesRepository : Repository {

    /**
     * Get available recipes.
     */
    fun getRecipes(): MutableList<Recipe>

    /**
     * Load the list of all available recipes in category with id that may be chosen by the user.
     */
    fun loadRecipesInCategory(recipeCategoryId: Long): Task<Unit>

    /**
     * Get the list of all available recipes in category with id.
     */
    fun getRecipeInCategoryById(id: Long): Task<RecipeDetails>

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
    fun getRecipesDetails(): MutableList<RecipeDetails>

    /**
     * Load the list of all available recipe details for recipe with id.
     */
    fun loadRecipeDetails(recipeId: Long): Task<RecipeDetails>

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
    fun loadRecipeTypes(recipeId: Long): Task<List<String>>

    /**
     * Load the list of all available recipe ingredients for recipe with id.
     */
    fun loadIngredients(recipeId: Long): Task<List<RecipeIngredient>>

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
    fun setIngredientSelected(recipeId: Long, ingredient: RecipeIngredient, isSelected: Boolean): Task<Unit>

    /**
     * Set for all ingredients for recipe with id property isInShoppingList equals isSelected
     */
    fun setAllIngredientsSelected(recipeId: Long, isSelected: Boolean): Task<Unit>

    /**
     * Determines if all ingredients are selected for recipe with id
     */
    fun isAllIngredientsSelected(recipeId: Long): Task<Boolean>

    /**
     * Load the list of all available recipe preparation steps for recipe with id.
     */
    fun loadPreparationSteps(recipeId: Long): Task<MutableList<RecipePreparationStep>>

}