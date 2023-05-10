package ksu.katara.healthymealplanner.model.recipes

import ksu.katara.healthymealplanner.model.recipes.entities.Recipe
import ksu.katara.healthymealplanner.model.recipes.entities.RecipeDetails
import ksu.katara.healthymealplanner.model.recipes.entities.RecipePreparationStep
import ksu.katara.healthymealplanner.tasks.Task

interface RecipesRepository {

    fun getRecipes(): MutableList<Recipe>

    fun getRecipeDetailsById(recipeId: Long): Task<RecipeDetails>

    fun loadRecipesInCategory(recipeCategoryId: Long): Task<Unit>

    fun getRecipeInCategoryById(id: Long): Task<RecipeDetails>

    fun addRecipeInCategoryListener(listener: RecipesInCategoryListener)

    fun removeRecipeInCategoryListener(listener: RecipesInCategoryListener)

    fun loadRecipeTypes(recipeId: Long): Task<List<String>>

    fun loadIngredients(recipeId: Long): Task<Unit>

    fun addIngredientListener(listener: RecipeIngredientsListener)

    fun removeIngredientsListener(listener: RecipeIngredientsListener)

    fun loadPreparationSteps(recipeId: Long): Task<MutableList<RecipePreparationStep>>

}