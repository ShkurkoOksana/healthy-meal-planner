package ksu.katara.healthymealplanner.mvvm.model.mealplan.entities

import ksu.katara.healthymealplanner.mvvm.model.meal.MealTypes
import ksu.katara.healthymealplanner.mvvm.model.recipes.entities.Recipe

/**
 * Represents meal plan recipes data
 */
data class MealPlanRecipes(
    val mealType: MealTypes,
    var recipesList: MutableList<Recipe>
)
