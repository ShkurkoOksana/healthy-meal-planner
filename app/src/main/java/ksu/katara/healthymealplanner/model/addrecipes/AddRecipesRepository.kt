package ksu.katara.healthymealplanner.model.addrecipes

import ksu.katara.healthymealplanner.model.meal.enum.MealTypes
import ksu.katara.healthymealplanner.model.recipes.entities.Recipe
import ksu.katara.healthymealplanner.tasks.Task

interface AddRecipesRepository {

    fun loadAddRecipes(mealTypes: MealTypes): Task<Unit>

    fun addAddRecipesListener(listener: AddRecipesListener)

    fun removeAddRecipesListener(listener: AddRecipesListener)

    fun addRecipesAddRecipe(id: Long): Task<Unit>

    fun addRecipesDeleteRecipe(recipe: Recipe): Task<Unit>

}