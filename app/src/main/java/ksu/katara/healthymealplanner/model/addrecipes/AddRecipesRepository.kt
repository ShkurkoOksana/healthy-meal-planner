package ksu.katara.healthymealplanner.model.addrecipes

import ksu.katara.healthymealplanner.model.meal.enum.MealTypes
import ksu.katara.healthymealplanner.model.recipes.entities.Recipe
import ksu.katara.healthymealplanner.tasks.Task

interface AddRecipesRepository {

    fun loadAddRecipes(selectedDate: String, mealTypes: MealTypes): Task<Unit>

    fun addAddRecipesListener(listener: AddRecipesListener)

    fun removeAddRecipesListener(listener: AddRecipesListener)

    fun addRecipesDeleteRecipe(recipe: Recipe): Task<Unit>

    //fun addRecipesAddRecipe(recipe: Recipe): Task<Unit>

}