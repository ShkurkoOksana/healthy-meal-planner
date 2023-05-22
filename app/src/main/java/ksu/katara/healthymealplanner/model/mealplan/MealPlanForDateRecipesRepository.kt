package ksu.katara.healthymealplanner.model.mealplan

import ksu.katara.healthymealplanner.model.meal.enum.MealTypes
import ksu.katara.healthymealplanner.model.mealplan.entities.MealPlanRecipes
import ksu.katara.healthymealplanner.model.recipes.entities.Recipe
import ksu.katara.healthymealplanner.tasks.Task

interface MealPlanForDateRecipesRepository {

    fun initMealPlan(): Task<Unit>

    fun getMealPlanForDate(): MutableMap<String, MutableList<MealPlanRecipes?>>

    fun loadMealPlanForDateRecipes(selectedDate: String, mealType: MealTypes): Task<Unit>

    fun addMealPlanForDateRecipesItemListener(listener: MealPlanForDateRecipesListener)

    fun removeMealPlanForDateRecipesItemListener(listener: MealPlanForDateRecipesListener)

    fun mealPlanForDateRecipesAddRecipe(selectedDate: String, mealType: MealTypes, recipe: Recipe): Task<Unit>

    fun mealPlanForDateRecipesDeleteRecipe(selectedDate: String, mealType: MealTypes, recipe: Recipe): Task<MealPlanRecipes?>

}
