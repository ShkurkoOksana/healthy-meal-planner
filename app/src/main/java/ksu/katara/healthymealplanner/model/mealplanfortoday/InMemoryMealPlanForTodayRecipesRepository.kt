package ksu.katara.healthymealplanner.model.mealplanfortoday

import ksu.katara.healthymealplanner.exceptions.MealPlanForTodayRecipesNotFoundException
import ksu.katara.healthymealplanner.model.meal.enum.MealTypes
import ksu.katara.healthymealplanner.model.mealplanfortoday.entities.MealPlanForTodayRecipes
import ksu.katara.healthymealplanner.model.recipes.entities.Recipe
import ksu.katara.healthymealplanner.tasks.SimpleTask
import ksu.katara.healthymealplanner.tasks.Task

typealias MealPlanForTodayRecipesListener = (mealPlanForTodayRecipes: MealPlanForTodayRecipes) -> Unit

class InMemoryMealPlanForTodayRecipesRepository : MealPlanForTodayRecipesRepository {

    private var mealPlanForToday = mutableListOf(
        MealPlanForTodayRecipes(MealTypes.BREAKFAST, mutableListOf()),
        MealPlanForTodayRecipes(MealTypes.LUNCH, mutableListOf()),
        MealPlanForTodayRecipes(MealTypes.DINNER, mutableListOf()),
        MealPlanForTodayRecipes(MealTypes.SNACK, mutableListOf()),
    )

    private lateinit var mealPlanForTodayRecipes: MealPlanForTodayRecipes

    private var mealPlanForTodayRecipesLoaded = false
    private val mealPlanForTodayRecipesListeners = mutableSetOf<MealPlanForTodayRecipesListener>()

    override fun loadMealPlanForTodayRecipes(mealType: MealTypes): Task<Unit> =
        SimpleTask {
            Thread.sleep(500)

            mealPlanForTodayRecipes = mealPlanForToday.firstOrNull {
                it.mealType == mealType
            } ?: throw MealPlanForTodayRecipesNotFoundException()

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

    override fun mealPlanForTodayRecipesAddRecipe(recipe: Recipe, mealType: MealTypes): Task<Unit> =
        SimpleTask {
            Thread.sleep(500)

            mealPlanForToday.firstOrNull{ it.mealType == mealType }?.recipesList?.add(recipe)

            notifyMealPlanForTodayChanges()
        }

    override fun mealPlanForTodayRecipesDeleteRecipe(recipeId: Long, mealType: MealTypes): Task<Unit> =
        SimpleTask {
            Thread.sleep(500)

            val indexToDelete = mealPlanForToday.firstOrNull{ it.mealType == mealType }?.recipesList!!.indexOfFirst { it.id == recipeId }
            if (indexToDelete != -1) {
                mealPlanForToday.firstOrNull{ it.mealType == mealType }?.recipesList!!.removeAt(indexToDelete)
                notifyMealPlanForTodayChanges()
            }
        }

    private fun notifyMealPlanForTodayChanges() {
        if (!mealPlanForTodayRecipesLoaded) return
        mealPlanForTodayRecipesListeners.forEach { it.invoke(mealPlanForTodayRecipes) }
    }
}