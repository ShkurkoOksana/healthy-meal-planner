package ksu.katara.healthymealplanner.model.mealplanfortoday

import ksu.katara.healthymealplanner.exceptions.MealPlanForTodayRecipesNotFoundException
import ksu.katara.healthymealplanner.model.meal.enum.MealTypes
import ksu.katara.healthymealplanner.model.mealplanfortoday.entities.MealPlanForTodayRecipes
import ksu.katara.healthymealplanner.model.recipes.RecipesRepository
import ksu.katara.healthymealplanner.model.recipes.entities.Recipe
import ksu.katara.healthymealplanner.tasks.SimpleTask
import ksu.katara.healthymealplanner.tasks.Task
import kotlin.random.Random

typealias MealPlanForTodayRecipesListener = (mealPlanForTodayRecipes: MealPlanForTodayRecipes) -> Unit

class InMemoryMealPlanForTodayRecipesRepository(
    private val recipesRepository: RecipesRepository
) : MealPlanForTodayRecipesRepository {

    private var mealPlanForToday: MutableList<MealPlanForTodayRecipes> = mutableListOf(
        MealPlanForTodayRecipes(0, MealTypes.BREAKFAST, mutableListOf(recipesRepository.getRecipeById(0))),
        MealPlanForTodayRecipes(0, MealTypes.LUNCH, mutableListOf(recipesRepository.getRecipeById(1))),
        MealPlanForTodayRecipes(0, MealTypes.SNACK, mutableListOf(recipesRepository.getRecipeById(2))),
        MealPlanForTodayRecipes(0, MealTypes.DINNER, mutableListOf(recipesRepository.getRecipeById(3))),
    )
    private lateinit var mealPlanForTodayRecipes: MealPlanForTodayRecipes
    private var mealPlanForTodayRecipesMealType: MealTypes? = null
    private var mealPlanForTodayRecipesLoaded = false
    private val mealPlanForTodayRecipesListeners = mutableSetOf<MealPlanForTodayRecipesListener>()

    override fun loadMealPlanForTodayRecipes(mealType: MealTypes): Task<Unit> =
        SimpleTask {
            Thread.sleep(2000)

            mealPlanForTodayRecipesMealType = mealType

            mealPlanForTodayRecipes = mealPlanForToday.firstOrNull{ it.mealType == mealType } ?: throw MealPlanForTodayRecipesNotFoundException()

            mealPlanForTodayRecipesLoaded = true
            notifyMealPlanForTodayChanges()
        }

    override fun addMealPlanForTodayRecipesItemListener(listener: MealPlanForTodayRecipesListener) {
        mealPlanForTodayRecipesListeners.add(listener)
        if (mealPlanForTodayRecipesLoaded) {
            listener.invoke(mealPlanForTodayRecipes)
        }
    }

    override fun removeMealPlanForTodayRecipesItemListener(listener: MealPlanForTodayRecipesListener) {
        mealPlanForTodayRecipesListeners.remove(listener)
    }

    override fun mealPlanForTodayRecipesAddRecipe(): Task<Unit> =
        SimpleTask {
            Thread.sleep(2000)

            val randomRecipe = recipesRepository.getRecipeById(Random.nextLong(0,4))

            mealPlanForTodayRecipes.recipesList.add(randomRecipe)
            notifyMealPlanForTodayChanges()
        }

    override fun mealPlanForTodayRecipesDeleteRecipe(recipe: Recipe): Task<Unit> =
        SimpleTask {
            Thread.sleep(2000)
            val indexToDelete = mealPlanForTodayRecipes.recipesList.indexOfFirst { it.id == recipe.id }
            if (indexToDelete != -1) {
                mealPlanForTodayRecipes.recipesList.removeAt(indexToDelete)
                notifyMealPlanForTodayChanges()
            }
        }

    private fun notifyMealPlanForTodayChanges() {
        if (!mealPlanForTodayRecipesLoaded) return
        mealPlanForTodayRecipesListeners.forEach { it.invoke(mealPlanForTodayRecipes) }
    }
}