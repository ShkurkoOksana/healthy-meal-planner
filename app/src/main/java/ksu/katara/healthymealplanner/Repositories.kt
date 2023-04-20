package ksu.katara.healthymealplanner

import ksu.katara.healthymealplanner.model.dietTips.DietTipsRepository
import ksu.katara.healthymealplanner.model.dietTips.InMemoryDietTipsRepository
import ksu.katara.healthymealplanner.model.mealplanfortoday.InMemoryMealPlanForTodayRecipesRepository
import ksu.katara.healthymealplanner.model.mealplanfortoday.MealPlanForTodayRecipesRepository
import ksu.katara.healthymealplanner.model.recipes.InMemoryRecipesRepository
import ksu.katara.healthymealplanner.model.recipes.RecipesRepository

object Repositories {

    val dietTipsRepository: DietTipsRepository = InMemoryDietTipsRepository()

    val recipeRepository: RecipesRepository = InMemoryRecipesRepository()

    val mealPlanForTodayRecipesRepository: MealPlanForTodayRecipesRepository = InMemoryMealPlanForTodayRecipesRepository(recipeRepository)

}
