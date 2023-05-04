package ksu.katara.healthymealplanner.model.addrecipes

import ksu.katara.healthymealplanner.exceptions.RecipeNotFoundException
import ksu.katara.healthymealplanner.model.meal.enum.MealTypes
import ksu.katara.healthymealplanner.model.recipes.RecipesRepository
import ksu.katara.healthymealplanner.model.recipes.entities.Recipe
import ksu.katara.healthymealplanner.tasks.SimpleTask
import ksu.katara.healthymealplanner.tasks.Task

typealias AddRecipesListener = (addRecipe: MutableList<Recipe>) -> Unit

class InMemoryAddRecipesRepository(
    recipesRepository: RecipesRepository,
) : AddRecipesRepository {

    private val allAddRecipesList = recipesRepository.getRecipes().map { it }.toMutableList()

    private val addRecipesToType: MutableMap<MealTypes, MutableList<Recipe>> = mutableMapOf(
        MealTypes.BREAKFAST to allAddRecipesList.map { it }.toMutableList(),
        MealTypes.LUNCH to allAddRecipesList.map { it }.toMutableList(),
        MealTypes.DINNER to allAddRecipesList.map { it }.toMutableList(),
        MealTypes.SNACK to allAddRecipesList.map { it }.toMutableList(),
    )

    private lateinit var addRecipes: MutableList<Recipe>
    private var addRecipesLoaded = false
    private val addRecipesListeners = mutableListOf<AddRecipesListener>()

    override fun loadAddRecipes(mealTypes: MealTypes): Task<Unit> =
        SimpleTask {
            Thread.sleep(500)

            addRecipes = addRecipesToType.getValue(mealTypes)

            addRecipesLoaded = true

            notifyAddRecipesChanges()
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

    override fun addRecipesAddRecipe(id: Long): Task<Unit> =
        SimpleTask {
            Thread.sleep(500)

            val recipe = allAddRecipesList.firstOrNull { it.id == id } ?: throw RecipeNotFoundException()

            addRecipes.add(recipe)

            notifyAddRecipesChanges()
        }

    override fun addRecipesDeleteRecipe(recipe: Recipe): Task<Unit> =
        SimpleTask {
            Thread.sleep(500)

            val deleteRecipe = allAddRecipesList.firstOrNull { it == recipe } ?: throw RecipeNotFoundException()

            addRecipes.remove(deleteRecipe)

            notifyAddRecipesChanges()
        }

    private fun notifyAddRecipesChanges() {
        if (!addRecipesLoaded) return
        addRecipesListeners.forEach { it.invoke(addRecipes) }
    }
}