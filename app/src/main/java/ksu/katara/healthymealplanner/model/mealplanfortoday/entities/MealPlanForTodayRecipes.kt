package ksu.katara.healthymealplanner.model.mealplanfortoday.entities

import ksu.katara.healthymealplanner.model.meal.enum.MealTypes
import ksu.katara.healthymealplanner.model.recipes.entities.Recipe

data class MealPlanForTodayRecipes(
    val id: Long,
    val mealType: MealTypes,
    var recipesList: MutableList<Recipe>
)
