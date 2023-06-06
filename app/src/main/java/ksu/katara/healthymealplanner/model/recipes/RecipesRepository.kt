package ksu.katara.healthymealplanner.model.recipes

import ksu.katara.healthymealplanner.model.recipes.entities.Recipe
import ksu.katara.healthymealplanner.model.recipes.entities.RecipeDetails
import ksu.katara.healthymealplanner.model.recipes.entities.RecipeIngredient
import ksu.katara.healthymealplanner.model.recipes.entities.RecipePreparationStep
import ksu.katara.healthymealplanner.tasks.Task

interface RecipesRepository {

    fun getRecipes(): MutableList<Recipe>

    fun getRecipesDetails(): MutableList<RecipeDetails>

    fun getRecipeDetailsById(recipeId: Long): Task<RecipeDetails>

    fun loadRecipesInCategory(recipeCategoryId: Long): Task<Unit>

    fun getRecipeInCategoryById(id: Long): Task<RecipeDetails>

    fun addRecipeInCategoryListener(listener: RecipesInCategoryListener)

    fun removeRecipeInCategoryListener(listener: RecipesInCategoryListener)

    fun loadRecipeTypes(recipeId: Long): Task<List<String>>

    fun loadIngredients(recipeId: Long): Task<List<RecipeIngredient>>

    fun addIngredientListener(listener: RecipeIngredientsListener)

    fun removeIngredientsListener(listener: RecipeIngredientsListener)

    fun setIngredientSelected(recipeId: Long, ingredient: RecipeIngredient, isSelected: Boolean): Task<Boolean>

    fun setAllIngredientsSelected(recipeId: Long, isSelected: Boolean): Task<Unit>

    fun isAllIngredientsSelected(recipeId: Long): Task<Boolean>

    fun loadPreparationSteps(recipeId: Long): Task<MutableList<RecipePreparationStep>>

}