package ksu.katara.healthymealplanner.mvvm.model.mealplan

import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import ksu.katara.healthymealplanner.foundation.model.coroutines.IoDispatcher
import ksu.katara.healthymealplanner.mvvm.model.mealplan.entities.MealPlanRecipes
import ksu.katara.healthymealplanner.mvvm.model.recipes.entities.Recipe
import ksu.katara.healthymealplanner.mvvm.views.main.tabs.home.MealTypes
import ksu.katara.healthymealplanner.mvvm.views.main.tabs.home.sdf
import java.util.Date

/**
 * Simple in-memory implementation of [MealPlanForDateRecipesRepository]
 */
class InMemoryMealPlanForDateRecipesRepository(
    private val ioDispatcher: IoDispatcher
) : MealPlanForDateRecipesRepository {

    private var mealPlanForDate: MutableMap<String, MutableList<MealPlanRecipes?>> = mutableMapOf()

    private var recipes: MealPlanRecipes? = null
    private var loaded = false
    private val listeners = mutableSetOf<MealPlanForDateRecipesListener>()

    override fun getMealPlan() = mealPlanForDate

    override suspend fun load(selectedDate: Date, mealType: MealTypes): MealPlanRecipes? = withContext(ioDispatcher.value) {
        delay(1000L)
        recipes = getRecipes(selectedDate, mealType)
        loaded = true
        notifyChanges()
        return@withContext recipes
    }

    private fun getRecipes(selectedDate: Date, mealType: MealTypes): MealPlanRecipes? {
        val date = sdf.format(selectedDate)
        val recipes: MealPlanRecipes? = if (mealPlanForDate.containsKey(date)) {
            mealPlanForDate
                .getValue(date)
                .firstOrNull { it?.mealType == mealType }
        } else {
            null
        }
        return recipes
    }

    override fun addListener(listener: MealPlanForDateRecipesListener) {
        listeners.add(listener)
        if (loaded) {
            listener.invoke(recipes)
        }
    }

    override fun removeListener(listener: MealPlanForDateRecipesListener) {
        listeners.remove(listener)
    }

    override suspend fun addRecipe(selectedDate: Date, mealType: MealTypes, recipe: Recipe) = withContext(ioDispatcher.value) {
        delay(1000L)
        addRecipeToMealPlanForDate(selectedDate, mealType, recipe)
        notifyChanges()
    }

    private fun addRecipeToMealPlanForDate(selectedDate: Date, mealType: MealTypes, recipe: Recipe) {
        val addRecipe = MealPlanRecipes(mealType, mutableListOf(recipe))
        val date = sdf.format(selectedDate)
        if (mealPlanForDate.containsKey(date)) {
            val mealPlan: MealPlanRecipes? = mealPlanForDate
                .getValue(date)
                .firstOrNull { it?.mealType == mealType }
            if (mealPlan == null) {
                recipes = addRecipe
                mealPlanForDate[date]?.add(addRecipe)
            } else {
                val index = mealPlanForDate[date]?.indexOfFirst { it == mealPlan }
                mealPlan.recipes.add(recipe)
                recipes = mealPlan
                mealPlanForDate[date]?.set(index!!, mealPlan)
            }
        } else {
            recipes = addRecipe
            mealPlanForDate[date] = mutableListOf(addRecipe)
        }
    }

    override suspend fun deleteRecipe(selectedDate: Date, mealType: MealTypes, recipe: Recipe): MealPlanRecipes? = withContext(ioDispatcher.value) {
        delay(1000L)
        deleteRecipeFromMealPlanForDate(selectedDate, mealType, recipe)
        notifyChanges()
        return@withContext recipes
    }

    private fun deleteRecipeFromMealPlanForDate(selectedDate: Date, mealType: MealTypes, recipe: Recipe) {
        var recipeList: MutableList<Recipe>
        val date = sdf.format(selectedDate)
        if (mealPlanForDate.containsKey(date)) {
            val mealPlan = mealPlanForDate.getValue(date)
            mealPlan.forEach { mealPlanRecipes ->
                if (mealPlanRecipes?.mealType == mealType) {
                    recipeList = mealPlanRecipes.recipes
                    val indexToDelete = recipeList.indexOfFirst { it == recipe }
                    if (indexToDelete != -1) {
                        recipeList.removeAt(indexToDelete)
                    }
                    recipes = if (recipeList.isNotEmpty()) {
                        mealPlanRecipes
                    } else {
                        mealPlanForDate.remove(date)
                        null
                    }
                }
            }
        }
    }

    private fun notifyChanges() {
        if (!loaded) return
        listeners.forEach { it.invoke(recipes) }
    }
}