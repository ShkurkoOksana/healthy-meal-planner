package ksu.katara.healthymealplanner.mvvm.domain.mealplan.entities

import ksu.katara.healthymealplanner.mvvm.domain.recipes.entities.Recipe
import ksu.katara.healthymealplanner.mvvm.presentation.main.tabs.home.MealTypes

/**
 * Represents meal plan recipes data
 */
data class MealPlanRecipes(
    val mealType: MealTypes,
    var recipes: MutableList<Recipe>
)
