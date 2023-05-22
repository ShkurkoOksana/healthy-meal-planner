package ksu.katara.healthymealplanner.model.mealplan

import ksu.katara.healthymealplanner.model.meal.enum.MealTypes
import ksu.katara.healthymealplanner.model.mealplan.entities.MealPlanRecipes
import ksu.katara.healthymealplanner.model.recipes.entities.Recipe
import ksu.katara.healthymealplanner.screens.main.tabs.home.sdf
import ksu.katara.healthymealplanner.tasks.SimpleTask
import ksu.katara.healthymealplanner.tasks.Task
import java.util.Date

typealias MealPlanForDateRecipesListener = (mealPlanRecipes: MealPlanRecipes?) -> Unit

class InMemoryMealPlanForDateRecipesRepository : MealPlanForDateRecipesRepository {
    private val currentDate = sdf.format(Date())

    private var mealPlanForDate: MutableMap<String, MutableList<MealPlanRecipes?>> = mutableMapOf()

    private var mealPlanForDateRecipes: MealPlanRecipes? = null

    private var mealPlanForDateRecipesLoaded = false
    private val mealPlanForDateRecipesListeners = mutableSetOf<MealPlanForDateRecipesListener>()

    override fun initMealPlan(): Task<Unit> =
        SimpleTask {
            Thread.sleep(500)

            mealPlanForDate = mutableMapOf(
                currentDate to mutableListOf(
                    MealPlanRecipes(
                        MealTypes.BREAKFAST, mutableListOf(
                            Recipe(
                                id = 0,
                                name = "Глазунья",
                                photo = "https://images.unsplash.com/photo-1612487439139-c2d7bac13577?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=2370&q=80",
                                categoryId = 1,
                            )
                        )
                    ),
                    null,
                    null,
                    MealPlanRecipes(
                        MealTypes.SNACK, mutableListOf(
                            Recipe(
                                id = 2,
                                name = "Борщ",
                                photo = "https://images.unsplash.com/photo-1527976746453-f363eac4d889?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=3432&q=80",
                                categoryId = 0,
                            )
                        )
                    ),
                ),
                "30-5-2023" to mutableListOf(
                    null,
                    null,
                    null,
                    MealPlanRecipes(
                        MealTypes.SNACK, mutableListOf(
                            Recipe(
                                id = 2,
                                name = "Греческий салат",
                                photo = "https://images.unsplash.com/photo-1625944230945-1b7dd3b949ab?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1760&q=80",
                                categoryId = 2,
                            )
                        )
                    ),
                )
            )
        }

    override fun getMealPlanForDate() = mealPlanForDate

    override fun loadMealPlanForDateRecipes(selectedDate: String, mealType: MealTypes): Task<Unit> =
        SimpleTask {
            Thread.sleep(500)

            val mealPlanForDateRecipesListItem: MealPlanRecipes?
            if (mealPlanForDate.containsKey(selectedDate)) {
                mealPlanForDateRecipesListItem = mealPlanForDate
                    .getValue(selectedDate)
                    .firstOrNull { it?.mealType == mealType }

                mealPlanForDateRecipes = mealPlanForDateRecipesListItem
            } else {
                mealPlanForDateRecipes = null
            }

            mealPlanForDateRecipesLoaded = true

            notifyMealPlanForDateChanges()
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
            Thread.sleep(500)

            val newMealPlanForTodayRecipesItem = MealPlanRecipes(mealType, mutableListOf(recipe))

            val mealPlanForDateRecipesListItem: MealPlanRecipes?
            if (mealPlanForDate.containsKey(selectedDate)) {
                mealPlanForDateRecipesListItem = mealPlanForDate
                    .getValue(selectedDate)
                    .firstOrNull { it?.mealType == mealType }

                if(mealPlanForDateRecipesListItem == null) {
                    mealPlanForDateRecipes = newMealPlanForTodayRecipesItem
                    mealPlanForDate[selectedDate]?.add(newMealPlanForTodayRecipesItem)
                } else {
                    val index = mealPlanForDate[selectedDate]?.indexOfFirst { it == mealPlanForDateRecipesListItem}
                    mealPlanForDateRecipesListItem.recipesList.add(recipe)
                    mealPlanForDateRecipes = mealPlanForDateRecipesListItem
                    mealPlanForDate[selectedDate]?.set(index!!, mealPlanForDateRecipesListItem)
                }
            } else {
                mealPlanForDateRecipes = newMealPlanForTodayRecipesItem
                mealPlanForDate[selectedDate] = mutableListOf(newMealPlanForTodayRecipesItem)
            }

            notifyMealPlanForDateChanges()
        }

    override fun mealPlanForDateRecipesDeleteRecipe(selectedDate: String, mealType: MealTypes, recipe: Recipe): Task<MealPlanRecipes?> =
        SimpleTask {
            Thread.sleep(500)

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

            mealPlanForDateRecipes
        }

    private fun notifyMealPlanForDateChanges() {
        if (!mealPlanForDateRecipesLoaded) return
        mealPlanForDateRecipesListeners.forEach { it.invoke(mealPlanForDateRecipes) }
    }
}