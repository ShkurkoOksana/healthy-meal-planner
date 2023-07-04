package ksu.katara.healthymealplanner.mvvm.model.mealplan.entities

import ksu.katara.healthymealplanner.mvvm.model.recipes.entities.Recipe
import ksu.katara.healthymealplanner.mvvm.views.main.tabs.home.MealTypes

/**
 * Represents meal plan recipes data
 */
data class MealPlanRecipes(
    val mealType: MealTypes,
    var recipesList: MutableList<Recipe>
)
