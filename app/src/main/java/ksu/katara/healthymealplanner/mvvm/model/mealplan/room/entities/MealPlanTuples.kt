package ksu.katara.healthymealplanner.mvvm.model.mealplan.room.entities

import androidx.room.Embedded
import ksu.katara.healthymealplanner.mvvm.model.recipes.room.entities.RecipeDBEntity

data class MealPlanAndRecipesTuple(
    @Embedded val recipeDBEntity: RecipeDBEntity,
    @Embedded val mealPlanRecipesJoinTableDBEntity: MealPlanRecipesJoinTableDBEntity,
    @Embedded val mealPlanDBEntity: MealPlanDBEntity,
    @Embedded val mealTypeDBEntity: MealTypeDBEntity,
)