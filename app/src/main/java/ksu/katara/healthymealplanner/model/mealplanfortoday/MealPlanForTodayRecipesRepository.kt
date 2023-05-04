package ksu.katara.healthymealplanner.model.mealplanfortoday

import ksu.katara.healthymealplanner.model.meal.enum.MealTypes
import ksu.katara.healthymealplanner.model.recipes.entities.Recipe
import ksu.katara.healthymealplanner.tasks.Task

interface MealPlanForTodayRecipesRepository {

    fun loadMealPlanForTodayRecipes(mealType: MealTypes): Task<Unit>

    fun addMealPlanForTodayRecipesItemListener(listener: MealPlanForTodayRecipesListener)

    fun removeMealPlanForTodayRecipesItemListener(listener: MealPlanForTodayRecipesListener)

    fun mealPlanForTodayRecipesAddRecipe(recipe: Recipe, mealType: MealTypes): Task<Unit>

    fun mealPlanForTodayRecipesDeleteRecipe(recipeId: Long, mealType: MealTypes): Task<Unit>

}
