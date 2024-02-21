package ksu.katara.healthymealplanner.mvvm.data.mealplan.entities

import androidx.room.Embedded
import ksu.katara.healthymealplanner.mvvm.data.recipes.entities.RecipeDBEntity

data class MealPlanAndRecipesTuple(
    @Embedded val recipeDBEntity: RecipeDBEntity,
    @Embedded val mealPlanRecipesJoinTableDBEntity: MealPlanRecipesJoinTableDBEntity,
    @Embedded val mealPlanDBEntity: MealPlanDBEntity,
    @Embedded val mealTypeDBEntity: MealTypeDBEntity,
)