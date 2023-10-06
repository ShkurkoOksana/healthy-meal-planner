package ksu.katara.healthymealplanner.mvvm.model.shoppinglist

import android.util.Log
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
    private var loaded = false
    private val listeners = mutableSetOf<ShoppingListListener>()

    override suspend fun load(): MutableList<ShoppingListRecipe> = withContext(ioDispatcher.value) {
        delay(1000L)
        loaded = true
        notifyChanges()
        return@withContext shoppingList
    }

    override fun listener(): Flow<MutableList<ShoppingListRecipe>> = callbackFlow {
        val listener: ShoppingListListener = {
            trySend(it)
        }
        listeners.add(listener)
        awaitClose {
            listeners.remove(listener)
        }
    }.buffer(Channel.CONFLATED)

    private fun notifyChanges() {
        if (!loaded) return
        listeners.forEach { it.invoke(shoppingList) }
    }

    override suspend fun addIngredient(recipeId: Long, ingredient: RecipeIngredient) =
        withContext(ioDispatcher.value) {
            delay(1000L)
            addIngredientToShoppingListRecipe(recipeId, ingredient)
            notifyChanges()
        }

    private fun addIngredientToShoppingListRecipe(recipeId: Long, ingredient: RecipeIngredient) {
        val recipeDetails =
            recipesRepository.getRecipesDetails().firstOrNull { it.recipe.id == recipeId }
                ?: throw RecipeDetailsNotFoundException()
        val recipe = shoppingList.firstOrNull { it.recipe.id == recipeId }
        if (recipe == null) {
            val ingredients = mutableListOf(
                ShoppingListRecipeIngredient(
                    ingredient = ingredient,
                    isSelectAndCross = false,
                )
            )
            shoppingList.add(
                ShoppingListRecipe(
                    recipe = recipeDetails.recipe,
                    ingredients = ingredients
                )
            )
        } else {
            val ingredients = recipe.ingredients
            val recipeIngredient = ingredients.firstOrNull { it.ingredient == ingredient }
            if (recipeIngredient == null) {
                ingredients.add(
                    ShoppingListRecipeIngredient(
                        ingredient = ingredient,
                        isSelectAndCross = false,
                    )
                )
            }
        }
    }

    override suspend fun addAllIngredients(recipeId: Long, isSelected: Boolean) =
        withContext(ioDispatcher.value) {
            delay(1000L)
            addAllIngredientsToShoppingListRecipe(recipeId, isSelected)
            notifyChanges()
        }

    private fun addAllIngredientsToShoppingListRecipe(recipeId: Long, isSelected: Boolean) {
        val recipe = recipesRepository.getRecipes().firstOrNull { it.id == recipeId }
            ?: throw RecipeNotFoundException()
        val allIngredients = recipesRepository.getRecipesDetails()
            .firstOrNull { it.recipe.id == recipeId }?.ingredients
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
                shoppingListRecipe.ingredients.clear()
                shoppingListRecipe.ingredients.addAll(allShoppingListRecipeIngredients)
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

    override suspend fun selectIngredient(
        recipe: ShoppingListRecipe,
        ingredient: ShoppingListRecipeIngredient,
        isChecked: Boolean,
    ) = withContext(ioDispatcher.value) {
        delay(1000L)
        val recipe =
            shoppingList.firstOrNull { it == recipe } ?: throw ShoppingListRecipeNotFoundException()
        val ingredient = recipe.ingredients.firstOrNull { it == ingredient }
            ?: throw IngredientsNotFoundException()
        ingredient.isSelectAndCross = isChecked
        notifyChanges()
    }

    override fun deleteIngredient(recipeId: Long, ingredient: RecipeIngredient): Flow<Int> =
        flow {
            var progress = 0
            while (progress < 100) {
                progress += 2
                delay(30)
                emit(progress)
            }
            deleteIngredientFromShoppingListIngredients(recipeId, ingredient)
            notifyChanges()
        }.flowOn(ioDispatcher.value)

    private fun deleteIngredientFromShoppingListIngredients(
        recipeId: Long,
        ingredient: RecipeIngredient
    ) {
        val recipe = shoppingList.firstOrNull { it.recipe.id == recipeId }
            ?: throw ShoppingListRecipeNotFoundException()
        val ingredients = recipe.ingredients
        val ingredient = ingredients.firstOrNull { it.ingredient == ingredient }
        val shoppingListItem =
            shoppingList.firstOrNull { it == recipe } ?: throw ShoppingListRecipeNotFoundException()
        val indexToDelete = shoppingListItem.ingredients.indexOfFirst { it == ingredient }
        if (indexToDelete != -1) {
            shoppingListItem.ingredients.removeAt(indexToDelete)
        }
        if (shoppingListItem.ingredients.isEmpty()) {
            val indexToDelete = shoppingList.indexOfFirst { it == recipe }
            if (indexToDelete != -1) {
                shoppingList.removeAt(indexToDelete)
            }
        }
    }
}
