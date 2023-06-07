package ksu.katara.healthymealplanner.model.mealplan

import ksu.katara.healthymealplanner.model.meal.enum.MealTypes
import ksu.katara.healthymealplanner.model.mealplan.entities.MealPlanRecipes
import ksu.katara.healthymealplanner.model.recipes.entities.Recipe
import ksu.katara.healthymealplanner.tasks.SimpleTask
import ksu.katara.healthymealplanner.tasks.Task

typealias MealPlanForDateRecipesListener = (mealPlanRecipes: MealPlanRecipes?) -> Unit

class InMemoryMealPlanForDateRecipesRepository : MealPlanForDateRecipesRepository {

    private var mealPlanForDate: MutableMap<String, MutableList<MealPlanRecipes?>> = mutableMapOf()

    private var mealPlanForDateRecipes: MealPlanRecipes? = null
    private var mealPlanForDateRecipesLoaded = false
    private val mealPlanForDateRecipesListeners = mutableSetOf<MealPlanForDateRecipesListener>()

    override fun loadMealPlan(): Task<Unit> =
        SimpleTask {
            Thread.sleep(200L)

            mealPlanForDate = mutableMapOf()
        }

    override fun getMealPlan() = mealPlanForDate

    override fun loadMealPlanForDateRecipes(selectedDate: String, mealType: MealTypes): Task<Unit> =
        SimpleTask {
            Thread.sleep(200L)

            mealPlanForDateRecipes = getMealPlanForDateRecipes(selectedDate, mealType)

            mealPlanForDateRecipesLoaded = true

            notifyMealPlanForDateChanges()
        }

    private fun getMealPlanForDateRecipes(selectedDate: String, mealType: MealTypes): MealPlanRecipes? {
        val mealPlanForDateRecipes: MealPlanRecipes? = if (mealPlanForDate.containsKey(selectedDate)) {
            mealPlanForDate
                .getValue(selectedDate)
                .firstOrNull { it?.mealType == mealType }
        } else {
            null
        }

        return mealPlanForDateRecipes
    }

    override fun addMealPlanForDateRecipesItemListener(listener: MealPlanForDateRecipesListener) {
        mealPlanForDateRecipesListeners.add(listener)
        if (mealPlanForDateRecipesLoaded) {
            listener.invoke(mealPlanForDateRecipes)
        }
    }

    override fun removeMealPlanForDateRecipesItemListener(listener: MealPlanForDateRecipesListener) {
        mealPlanForDateRecipesListeners.remove(listener)
    }

    override fun mealPlanForDateRecipesAddRecipe(selectedDate: String, mealType: MealTypes, recipe: Recipe): Task<Unit> =
        SimpleTask {
            Thread.sleep(200L)

            addRecipeToMealPlanForDate(selectedDate, mealType, recipe)

            notifyMealPlanForDateChanges()
        }

    private fun addRecipeToMealPlanForDate(selectedDate: String, mealType: MealTypes, recipe: Recipe) {
        val newMealPlanForTodayRecipesItem = MealPlanRecipes(mealType, mutableListOf(recipe))

        val mealPlanForDateRecipesListItem: MealPlanRecipes?
        if (mealPlanForDate.containsKey(selectedDate)) {
            mealPlanForDateRecipesListItem = mealPlanForDate
                .getValue(selectedDate)
                .firstOrNull { it?.mealType == mealType }

            if (mealPlanForDateRecipesListItem == null) {
                mealPlanForDateRecipes = newMealPlanForTodayRecipesItem
                mealPlanForDate[selectedDate]?.add(newMealPlanForTodayRecipesItem)
            } else {
                val index = mealPlanForDate[selectedDate]?.indexOfFirst { it == mealPlanForDateRecipesListItem }
                mealPlanForDateRecipesListItem.recipesList.add(recipe)
                mealPlanForDateRecipes = mealPlanForDateRecipesListItem
                mealPlanForDate[selectedDate]?.set(index!!, mealPlanForDateRecipesListItem)
            }
        } else {
            mealPlanForDateRecipes = newMealPlanForTodayRecipesItem
            mealPlanForDate[selectedDate] = mutableListOf(newMealPlanForTodayRecipesItem)
        }
    }

    override fun mealPlanForDateRecipesDeleteRecipe(selectedDate: String, mealType: MealTypes, recipe: Recipe): Task<MealPlanRecipes?> =
        SimpleTask {
            Thread.sleep(200L)

            deleteRecipeFromMealPlanForDate(selectedDate, mealType, recipe)

            mealPlanForDateRecipes
        }

    private fun deleteRecipeFromMealPlanForDate(selectedDate: String, mealType: MealTypes, recipe: Recipe) {
        var recipeList: MutableList<Recipe>

        if (mealPlanForDate.containsKey(selectedDate)) {
            val mealPlanForDateRecipesList = mealPlanForDate.getValue(selectedDate)
            mealPlanForDateRecipesList.forEach { mealPlanForDateRecipesListItem ->
                if (mealPlanForDateRecipesListItem?.mealType == mealType) {
                    recipeList = mealPlanForDateRecipesListItem.recipesList

                    val indexToDelete = recipeList.indexOfFirst { it == recipe }
                    if (indexToDelete != -1) {
                        recipeList.removeAt(indexToDelete)
                    }

                    mealPlanForDateRecipes = if (recipeList.isNotEmpty()) {
                        mealPlanForDateRecipesListItem
                    } else {
                        null
                    }
                }
            }
        }
    }

    private fun notifyMealPlanForDateChanges() {
        if (!mealPlanForDateRecipesLoaded) return
        mealPlanForDateRecipesListeners.forEach { it.invoke(mealPlanForDateRecipes) }
    }
}