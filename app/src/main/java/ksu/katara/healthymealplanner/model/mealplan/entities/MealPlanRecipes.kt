package ksu.katara.healthymealplanner.model.mealplan.entities

import ksu.katara.healthymealplanner.model.meal.enum.MealTypes
import ksu.katara.healthymealplanner.model.recipes.entities.Recipe

data class MealPlanRecipes(
    val mealType: MealTypes,
    var recipesList: MutableList<Recipe>
)
