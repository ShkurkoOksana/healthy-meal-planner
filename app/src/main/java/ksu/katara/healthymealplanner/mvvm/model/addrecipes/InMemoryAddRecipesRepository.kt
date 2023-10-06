package ksu.katara.healthymealplanner.mvvm.model.addrecipes

import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import ksu.katara.healthymealplanner.foundation.model.coroutines.IoDispatcher
import ksu.katara.healthymealplanner.mvvm.model.mealplan.MealPlanForDateRecipesRepository
import ksu.katara.healthymealplanner.mvvm.model.recipes.RecipesRepository
import ksu.katara.healthymealplanner.mvvm.model.recipes.entities.Recipe
import ksu.katara.healthymealplanner.mvvm.views.main.tabs.home.MealTypes
import ksu.katara.healthymealplanner.mvvm.views.main.tabs.home.sdf
import java.util.Date

/**
 * Simple in-memory implementation of [AddRecipesRepository]
 */
class InMemoryAddRecipesRepository(
    private val recipesRepository: RecipesRepository,
    private val mealPlanForDateRecipesRepository: MealPlanForDateRecipesRepository,
    private val ioDispatcher: IoDispatcher
) : AddRecipesRepository {
    private lateinit var addRecipes: MutableList<Recipe>
    private var loaded = false
    private val listeners = mutableListOf<AddRecipesListener>()

    override suspend fun load(selectedDate: Date, mealType: MealTypes): List<Recipe> = withContext(ioDispatcher.value) {
        delay(1000L)
        val mealPlanForDateRecipes: MutableList<Recipe> = getMealPlanForDateRecipes(selectedDate, mealType)
        addRecipes = getAddRecipes(mealPlanForDateRecipes)
        loaded = true
        notifyChanges()
        return@withContext addRecipes
    }

    private fun getAddRecipes(list: MutableList<Recipe>): MutableList<Recipe> {
        val recipes = recipesRepository.getRecipes().map { it }.toMutableList()
        list.forEach { recipe ->
            recipes.removeIf { it == recipe }
        }
        return recipes
    }

    private fun getMealPlanForDateRecipes(selectedDate: Date, mealType: MealTypes): MutableList<Recipe> {
        var mealPlanForDateRecipes: MutableList<Recipe> = mutableListOf()
        val mealPlanForDate = mealPlanForDateRecipesRepository.getMealPlan()
        val date = sdf.format(selectedDate)
        if (mealPlanForDate.containsKey(date)) {
            mealPlanForDate.getValue(date).forEach { mealPlanRecipes ->
                if (mealPlanRecipes?.mealType == mealType) {
                    mealPlanForDateRecipes = mealPlanRecipes.recipes
                }
            }
        } else {
            mealPlanForDateRecipes = mutableListOf()
        }
        return mealPlanForDateRecipes
    }

    override fun addListener(listener: AddRecipesListener) {
        listeners.add(listener)
        if (loaded) {
            listener.invoke(addRecipes)
        }
    }

    override fun removeListener(listener: AddRecipesListener) {
        listeners.remove(listener)
    }

    override suspend fun deleteRecipe(recipe: Recipe) = withContext(ioDispatcher.value) {
        delay(1000L)
        val indexToDelete = addRecipes.indexOfFirst { it == recipe }
        if (indexToDelete != -1) {
            addRecipes.removeAt(indexToDelete)
        }
        notifyChanges()
    }

    private fun notifyChanges() {
        if (!loaded) return
        listeners.forEach { it.invoke(addRecipes) }
    }
}