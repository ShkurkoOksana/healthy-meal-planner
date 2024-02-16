package ksu.katara.healthymealplanner.mvvm.model.shoppinglist

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import ksu.katara.healthymealplanner.foundation.model.coroutines.IoDispatcher
import ksu.katara.healthymealplanner.mvvm.model.recipes.entities.RecipeIngredient
import ksu.katara.healthymealplanner.mvvm.model.recipes.room.RecipesDao
import ksu.katara.healthymealplanner.mvvm.model.shoppinglist.entity.ShoppingListRecipe
import ksu.katara.healthymealplanner.mvvm.model.shoppinglist.entity.ShoppingListRecipeIngredient
import ksu.katara.healthymealplanner.mvvm.model.shoppinglist.room.ShoppingListDao
import javax.inject.Inject

/**
 * Simple in-memory implementation of [ShoppingListRepository]
 */
class RoomShoppingListRepository @Inject constructor(
    private val shoppingListDao: ShoppingListDao,
    private val recipesDao: RecipesDao,
    private val ioDispatcher: IoDispatcher
) : ShoppingListRepository {

    private var shoppingList: MutableList<ShoppingListRecipe> = mutableListOf()
    private var shoppingListLoaded = false
    private val shoppingListListeners = mutableSetOf<ShoppingListListener>()

    override suspend fun loadShoppingList(): MutableList<ShoppingListRecipe> =
        withContext(ioDispatcher.value) {
            delay(1000L)
            shoppingList = findShoppingList()
            shoppingListLoaded = true
            shoppingListNotifyChanges()
            return@withContext shoppingList
        }

    private fun findShoppingList(): MutableList<ShoppingListRecipe> {
        val shoppingList: MutableList<ShoppingListRecipe> = mutableListOf()
        val recipes = recipesDao.findRecipes().map { it.toRecipe() }
        for (recipe in recipes) {
            val ingredients = findShoppingListIngredients(recipe.id)
            val shoppingListRecipe = ShoppingListRecipe(
                recipe = recipe,
                ingredients = ingredients
            )
            if (ingredients.isNotEmpty()) {
                shoppingList.add(shoppingListRecipe)
            }
        }
        return shoppingList
    }

    private fun findShoppingListIngredients(recipeId: Long): MutableList<ShoppingListRecipeIngredient> {
        val tupleList = shoppingListDao.findShoppingListIngredients(recipeId)
        val list: MutableList<ShoppingListRecipeIngredient> = mutableListOf()
        tupleList.forEach {
            val shoppingListIngredient = ShoppingListRecipeIngredient(
                ingredient = RecipeIngredient(
                    id = it.recipeIngredientDBEntity.id,
                    product = it.recipeIngredientDBEntity.name,
                    amount = it.recipeIngredientsJoinTableDBEntity.amount.toDoubleOrNull() ?: 0.0,
                    measure = it.ingredientMeasureDBEntity.name,
                    isInShoppingList = it.recipeIngredientsJoinTableDBEntity.isInShoppingList == 1,
                ),
                isSelectAndCross = it.recipeIngredientsJoinTableDBEntity.isCrossInShoppingList == 1
            )
            list.add(shoppingListIngredient)
        }
        return list
    }

    override fun shoppingListListener(): Flow<MutableList<ShoppingListRecipe>> = callbackFlow {
        val listener: ShoppingListListener = {
            trySend(it)
        }
        shoppingListListeners.add(listener)
        awaitClose {
            shoppingListListeners.remove(listener)
        }
    }.buffer(Channel.CONFLATED)

    private fun shoppingListNotifyChanges() {
        if (!shoppingListLoaded) return
        shoppingListListeners.forEach { it.invoke(shoppingList) }
    }

    override suspend fun selectIngredient(
        shoppingListRecipe: ShoppingListRecipe,
        shoppingListIngredient: ShoppingListRecipeIngredient,
        isChecked: Boolean,
    ) = withContext(ioDispatcher.value) {
        delay(1000L)
        updateShoppingListIngredient(
            shoppingListRecipe.recipe.id,
            shoppingListIngredient.ingredient.id,
            if (isChecked) "1" else "0"
        )
        shoppingList = findShoppingList()
        shoppingListLoaded = true
        shoppingListNotifyChanges()
    }

    private fun updateShoppingListIngredient(
        recipeId: Long,
        ingredientId: Long,
        isChecked: String
    ) {
        shoppingListDao.updateShoppingListIngredient(recipeId, ingredientId, isChecked)
    }

    override fun deleteIngredient(recipeId: Long, ingredient: RecipeIngredient): Flow<Int> =
        flow {
            var progress = 0
            while (progress < 100) {
                progress += 2
                delay(30)
                emit(progress)
            }
            deleteShoppingListIngredient(recipeId, ingredient.id)
            shoppingList = findShoppingList()
            shoppingListLoaded = true
            shoppingListNotifyChanges()
        }.flowOn(ioDispatcher.value)

    private fun deleteShoppingListIngredient(recipeId: Long, ingredientId: Long) {
        shoppingListDao.deleteShoppingListIngredient(recipeId, ingredientId)
    }

}
