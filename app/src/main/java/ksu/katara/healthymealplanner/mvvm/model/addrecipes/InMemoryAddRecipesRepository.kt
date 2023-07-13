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
    private var addRecipesLoaded = false
    private val addRecipesListeners = mutableListOf<AddRecipesListener>()

    override suspend fun loadAddRecipes(selectedDate: Date, mealType: MealTypes): List<Recipe> = withContext(ioDispatcher.value) {
        delay(1000L)
        val mealPlanForDateRecipesList: MutableList<Recipe> = getMealPlanForDateRecipesList(selectedDate, mealType)
        addRecipes = getAddRecipes(mealPlanForDateRecipesList)
        addRecipesLoaded = true
        notifyAddRecipesChanges()
        return@withContext addRecipes
    }

    private fun getAddRecipes(list: MutableList<Recipe>): MutableList<Recipe> {
        val recipesList = recipesRepository.getRecipes().map { it }.toMutableList()
        list.forEach { mealPlanForDateRecipesListItem ->
            recipesList.removeIf { it == mealPlanForDateRecipesListItem }
        }
        return recipesList
    }

    private fun getMealPlanForDateRecipesList(selectedDate: Date, mealType: MealTypes): MutableList<Recipe> {
        return getMealPlanForDateRecipes(selectedDate, mealType)
    }

    private fun getMealPlanForDateRecipes(selectedDate: Date, mealType: MealTypes): MutableList<Recipe> {
        var mealPlanForDateRecipesList: MutableList<Recipe> = mutableListOf()
        val mealPlanForDate = mealPlanForDateRecipesRepository.getMealPlan()
        val date = sdf.format(selectedDate)
        if (mealPlanForDate.containsKey(date)) {
            mealPlanForDate.getValue(date).forEach { mealPlanForDateRecipes ->
                if (mealPlanForDateRecipes?.mealType == mealType) {
                    mealPlanForDateRecipesList = mealPlanForDateRecipes.recipesList
                }
            }
        } else {
            mealPlanForDateRecipesList = mutableListOf()
        }
        return mealPlanForDateRecipesList
    }

    override fun addAddRecipesListener(listener: AddRecipesListener) {
        addRecipesListeners.add(listener)
        if (addRecipesLoaded) {
            listener.invoke(addRecipes)
        }
    }

    override fun removeAddRecipesListener(listener: AddRecipesListener) {
        addRecipesListeners.remove(listener)
    }

    override suspend fun addRecipesDeleteRecipe(recipe: Recipe) = withContext(ioDispatcher.value) {
        delay(1000L)
        val indexToDelete = addRecipes.indexOfFirst { it == recipe }
        if (indexToDelete != -1) {
            addRecipes.removeAt(indexToDelete)
        }
        notifyAddRecipesChanges()
    }

    private fun notifyAddRecipesChanges() {
        if (!addRecipesLoaded) return
        addRecipesListeners.forEach { it.invoke(addRecipes) }
    }
}