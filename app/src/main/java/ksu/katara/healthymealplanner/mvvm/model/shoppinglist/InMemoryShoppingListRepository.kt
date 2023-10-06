package ksu.katara.healthymealplanner.mvvm.model.shoppinglist

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import ksu.katara.healthymealplanner.foundation.model.coroutines.IoDispatcher
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
    private val ioDispatcher: IoDispatcher
) : ShoppingListRepository {

    private var shoppingList: MutableList<ShoppingListRecipe> = mutableListOf()
    private var shoppingListLoaded = false
    private val shoppingListListeners = mutableSetOf<ShoppingListListener>()

    override suspend fun loadShoppingList(): MutableList<ShoppingListRecipe> = withContext(ioDispatcher.value) {
        delay(1000L)
        shoppingListLoaded = true
        notifyShoppingListChanges()
        return@withContext shoppingList
    }

    override fun listenShoppingListIngredients(): Flow<MutableList<ShoppingListRecipe>> = callbackFlow {
        val listener: ShoppingListListener = {
            trySend(it)
        }
        shoppingListListeners.add(listener)
        awaitClose {
            shoppingListListeners.remove(listener)
        }
    }.buffer(Channel.CONFLATED)

    private fun notifyShoppingListChanges() {
        if (!shoppingListLoaded) return
        shoppingListListeners.forEach { it.invoke(shoppingList) }
    }

    override suspend fun shoppingListIngredientsAddIngredient(recipeId: Long, recipeIngredient: RecipeIngredient) = withContext(ioDispatcher.value) {
            delay(1000L)
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

    override suspend fun shoppingListIngredientsAddAllIngredients(recipeId: Long, isSelected: Boolean) = withContext(ioDispatcher.value) {
        delay(1000L)
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

    override suspend fun shoppingListIngredientsSelectIngredient(
        shoppingListRecipe: ShoppingListRecipe,
        shoppingListRecipeIngredient: ShoppingListRecipeIngredient,
        isChecked: Boolean,
    ) = withContext(ioDispatcher.value) {
        delay(1000L)
        val shoppingListItem = shoppingList.firstOrNull { it == shoppingListRecipe } ?: throw ShoppingListRecipeNotFoundException()
        val shoppingListRecipeIngredientsItem =
            shoppingListItem.shoppingListIngredients.firstOrNull { it == shoppingListRecipeIngredient } ?: throw IngredientsNotFoundException()
        shoppingListRecipeIngredientsItem.isSelectAndCross = isChecked
        notifyShoppingListChanges()
    }

    override fun shoppingListIngredientsDeleteIngredient(
        recipeId: Long,
        ingredient: RecipeIngredient,
    ): Flow<Int> = flow {
        var progress = 0
        while (progress < 100) {
            progress += 2
            delay(30)
            emit(progress)
        }
        deleteIngredientFromShoppingListIngredients(recipeId, ingredient)
        notifyShoppingListChanges()
    }.flowOn(ioDispatcher.value)

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
