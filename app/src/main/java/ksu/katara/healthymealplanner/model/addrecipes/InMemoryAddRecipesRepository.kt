package ksu.katara.healthymealplanner.model.addrecipes

import ksu.katara.healthymealplanner.model.meal.enum.MealTypes
import ksu.katara.healthymealplanner.model.mealplan.MealPlanForDateRecipesRepository
import ksu.katara.healthymealplanner.model.recipes.RecipesRepository
import ksu.katara.healthymealplanner.model.recipes.entities.Recipe
import ksu.katara.healthymealplanner.tasks.SimpleTask
import ksu.katara.healthymealplanner.tasks.Task

typealias AddRecipesListener = (addRecipes: List<Recipe>) -> Unit

class InMemoryAddRecipesRepository(
    private val recipesRepository: RecipesRepository,
    private val mealPlanForDateRecipesRepository: MealPlanForDateRecipesRepository,
) : AddRecipesRepository {

    private lateinit var addRecipes: MutableList<Recipe>
    private var addRecipesLoaded = false
    private val addRecipesListeners = mutableListOf<AddRecipesListener>()

    override fun loadAddRecipes(selectedDate: String, mealType: MealTypes): Task<Unit> =
        SimpleTask {
            Thread.sleep(200L)

            val mealPlanForDateRecipesList: MutableList<Recipe> = getMealPlanForDateRecipesList(selectedDate, mealType)
            addRecipes = getAddRecipes(mealPlanForDateRecipesList)
            addRecipesLoaded = true
            notifyAddRecipesChanges()
        }

    private fun getAddRecipes(list: MutableList<Recipe>): MutableList<Recipe> {
        val allRecipesList = recipesRepository.getRecipes().map { it }.toMutableList()
        list.forEach { mealPlanForDateRecipesListItem ->
            allRecipesList.removeIf { it == mealPlanForDateRecipesListItem }
        }

        return allRecipesList
    }

    private fun getMealPlanForDateRecipesList(selectedDate: String, mealType: MealTypes): MutableList<Recipe> {
        return getMealPlanForDateRecipes(selectedDate, mealType)
    }

    private fun getMealPlanForDateRecipes(selectedDate: String, mealType: MealTypes): MutableList<Recipe> {
        var mealPlanForDateRecipesList: MutableList<Recipe> = mutableListOf()

        val mealPlanForDate = mealPlanForDateRecipesRepository.getMealPlan()
        if (mealPlanForDate.containsKey(selectedDate)) {
            mealPlanForDate.getValue(selectedDate).forEach { mealPlanForDateRecipes ->
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

    override fun addRecipesDeleteRecipe(recipe: Recipe): Task<Unit> =
        SimpleTask {
            Thread.sleep(200L)

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