package ksu.katara.healthymealplanner.mvvm.model.shoppinglist

import ksu.katara.healthymealplanner.foundation.tasks.SimpleTask
import ksu.katara.healthymealplanner.foundation.tasks.Task
import ksu.katara.healthymealplanner.mvvm.model.IngredientsNotFoundException
import ksu.katara.healthymealplanner.mvvm.model.RecipeDetailsNotFoundException
import ksu.katara.healthymealplanner.mvvm.model.RecipeNotFoundException
import ksu.katara.healthymealplanner.mvvm.model.ShoppingListRecipeNotFoundException
import ksu.katara.healthymealplanner.mvvm.model.recipes.RecipesRepository
import ksu.katara.healthymealplanner.mvvm.model.recipes.entities.RecipeIngredient
import ksu.katara.healthymealplanner.mvvm.model.shoppinglist.entity.ShoppingListRecipe
import ksu.katara.healthymealplanner.mvvm.model.shoppinglist.entity.ShoppingListRecipeIngredient

/**
 * Simple in-memory implementation of [ShoppingListRepository]
 */
class InMemoryShoppingListRepository(
    private val recipesRepository: RecipesRepository,
) : ShoppingListRepository {

    private var shoppingList: MutableList<ShoppingListRecipe> = mutableListOf()
    private var shoppingListLoaded = false
    private val shoppingListListeners = mutableSetOf<ShoppingListListener>()

    override fun loadShoppingList(): Task<MutableList<ShoppingListRecipe>> = SimpleTask {
        Thread.sleep(2000L)
        shoppingListLoaded = true
        notifyShoppingListChanges()
        return@SimpleTask shoppingList
    }

    override fun addShoppingListListener(listener: ShoppingListListener) {
        shoppingListListeners.add(listener)
        if (shoppingListLoaded) {
            listener.invoke(shoppingList)
        }
    }

    override fun removeShoppingListListener(listener: ShoppingListListener) {
        shoppingListListeners.remove(listener)
    }

    private fun notifyShoppingListChanges() {
        if (!shoppingListLoaded) return
        shoppingListListeners.forEach { it.invoke(shoppingList) }
    }

    override fun shoppingListIngredientsAddIngredient(recipeId: Long, recipeIngredient: RecipeIngredient): Task<Unit> =
        SimpleTask {
            Thread.sleep(2000L)
            addIngredientToShoppingListIngredients(recipeId, recipeIngredient)
            notifyShoppingListChanges()
        }

    private fun addIngredientToShoppingListIngredients(recipeId: Long, recipeIngredient: RecipeIngredient) {
        val recipeDetails =
            recipesRepository.getRecipesDetails().firstOrNull { it.recipe.id == recipeId } ?: throw RecipeDetailsNotFoundException()
        val shoppingListRecipe = shoppingList.firstOrNull { shoppingListRecipe -> shoppingListRecipe.recipe.id == recipeId }
        if (shoppingListRecipe == null) {
            val shoppingListItemIngredients = mutableListOf(
                ShoppingListRecipeIngredient(
                    recipeIngredient = recipeIngredient,
                    isSelectAndCross = false,
                )
            )
            shoppingList.add(
                ShoppingListRecipe(
                    recipe = recipeDetails.recipe,
                    shoppingListIngredients = shoppingListItemIngredients
                )
            )
        } else {
            val shoppingListRecipeIngredients = shoppingListRecipe.shoppingListIngredients
            val shoppingListRecipeIngredient = shoppingListRecipeIngredients.firstOrNull { it.recipeIngredient == recipeIngredient }
            if (shoppingListRecipeIngredient == null) {
                shoppingListRecipeIngredients.add(
                    ShoppingListRecipeIngredient(
                        recipeIngredient = recipeIngredient,
                        isSelectAndCross = false,
                    )
                )
            }
        }
    }

    override fun shoppingListIngredientsAddAllIngredients(recipeId: Long, isSelected: Boolean): Task<Unit> = SimpleTask {
        Thread.sleep(2000L)
        addAllIngredientsToShoppingListIngredients(recipeId, isSelected)
        notifyShoppingListChanges()
    }

    private fun addAllIngredientsToShoppingListIngredients(recipeId: Long, isSelected: Boolean) {
        val recipe = recipesRepository.getRecipes().firstOrNull { it.id == recipeId } ?: throw RecipeNotFoundException()
        val allIngredients = recipesRepository.getRecipesDetails().firstOrNull { it.recipe.id == recipeId }?.ingredients
            ?: throw RecipeDetailsNotFoundException()
        val shoppingListRecipe = shoppingList.firstOrNull { it.recipe.id == recipeId }
        val allShoppingListRecipeIngredients = allIngredients.map {
            ShoppingListRecipeIngredient(
                it,
                false,
            )
        }.toMutableList()
        val shoppingListRecipeWithAllIngredients = ShoppingListRecipe(
            recipe,
            allShoppingListRecipeIngredients,
        )
        if (isSelected) {
            if (shoppingListRecipe == null) {
                shoppingList.add(shoppingListRecipeWithAllIngredients)
            } else {
                shoppingListRecipe.shoppingListIngredients.clear()
                shoppingListRecipe.shoppingListIngredients.addAll(allShoppingListRecipeIngredients)
            }
        } else {
            if (shoppingListRecipe != null) {
                val indexToDelete = shoppingList.indexOfFirst { it == shoppingListRecipe }
                if (indexToDelete != -1) {
                    shoppingList.removeAt(indexToDelete)
                }
            }
        }
    }

    override fun shoppingListIngredientsSelectIngredient(
        shoppingListRecipe: ShoppingListRecipe,
        shoppingListRecipeIngredient: ShoppingListRecipeIngredient,
        isChecked: Boolean,
    ): Task<Unit> = SimpleTask {
        Thread.sleep(2000L)
        val shoppingListItem = shoppingList.firstOrNull { it == shoppingListRecipe } ?: throw ShoppingListRecipeNotFoundException()
        val shoppingListRecipeIngredientsItem =
            shoppingListItem.shoppingListIngredients.firstOrNull { it == shoppingListRecipeIngredient } ?: throw IngredientsNotFoundException()
        shoppingListRecipeIngredientsItem.isSelectAndCross = isChecked
        notifyShoppingListChanges()
    }

    override fun shoppingListIngredientsDeleteIngredient(
        recipeId: Long,
        ingredient: RecipeIngredient,
    ): Task<Unit> = SimpleTask {
        Thread.sleep(2000L)
        deleteIngredientFromShoppingListIngredients(recipeId, ingredient)
        notifyShoppingListChanges()
    }

    private fun deleteIngredientFromShoppingListIngredients(recipeId: Long, ingredient: RecipeIngredient) {
        val shoppingListRecipe = shoppingList.firstOrNull { it.recipe.id == recipeId } ?: throw ShoppingListRecipeNotFoundException()
        val shoppingListIngredients = shoppingListRecipe.shoppingListIngredients
        val shoppingListRecipeIngredient = shoppingListIngredients.firstOrNull { it.recipeIngredient == ingredient }
        val shoppingListItem = shoppingList.firstOrNull { it == shoppingListRecipe } ?: throw ShoppingListRecipeNotFoundException()
        val indexToDelete = shoppingListItem.shoppingListIngredients.indexOfFirst { it == shoppingListRecipeIngredient }
        if (indexToDelete != -1) {
            shoppingListItem.shoppingListIngredients.removeAt(indexToDelete)
        }

        if (shoppingListItem.shoppingListIngredients.isEmpty()) {
            val indexToDelete = shoppingList.indexOfFirst { it == shoppingListRecipe }
            if (indexToDelete != -1) {
                shoppingList.removeAt(indexToDelete)
            }
        }
    }
}
