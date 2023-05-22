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
            Thread.sleep(500)

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
        val mealPlanForDate = mealPlanForDateRecipesRepository.getMealPlanForDate()
        var mealPlanForDateRecipesList: MutableList<Recipe> = mutableListOf()
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
            Thread.sleep(500)

            val indexToRemove = addRecipes.indexOfFirst { it == recipe }
            if (indexToRemove != -1) {
                addRecipes.removeAt(indexToRemove)
            }

            notifyAddRecipesChanges()
        }

    private fun notifyAddRecipesChanges() {
        if (!addRecipesLoaded) return
        addRecipesListeners.forEach { it.invoke(addRecipes) }
    }
}