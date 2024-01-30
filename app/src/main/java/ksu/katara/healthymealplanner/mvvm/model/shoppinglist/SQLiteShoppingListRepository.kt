package ksu.katara.healthymealplanner.mvvm.model.shoppinglist

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.util.Log
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
import ksu.katara.healthymealplanner.mvvm.model.IngredientsNotFoundException
import ksu.katara.healthymealplanner.mvvm.model.ShoppingListRecipeNotFoundException
import ksu.katara.healthymealplanner.mvvm.model.recipes.entities.Recipe
import ksu.katara.healthymealplanner.mvvm.model.recipes.entities.RecipeIngredient
import ksu.katara.healthymealplanner.mvvm.model.shoppinglist.entity.ShoppingListRecipe
import ksu.katara.healthymealplanner.mvvm.model.shoppinglist.entity.ShoppingListRecipeIngredient
import ksu.katara.healthymealplanner.mvvm.model.sqlite.AppSQLiteContract.IngredientMeasuresTable
import ksu.katara.healthymealplanner.mvvm.model.sqlite.AppSQLiteContract.RecipeIngredientsIngredientMeasuresJoinTable
import ksu.katara.healthymealplanner.mvvm.model.sqlite.AppSQLiteContract.RecipeIngredientsTable
import ksu.katara.healthymealplanner.mvvm.model.sqlite.AppSQLiteContract.RecipesIngredientsJoinTable
import ksu.katara.healthymealplanner.mvvm.model.sqlite.AppSQLiteContract.RecipesTable

/**
 * Simple in-memory implementation of [ShoppingListRepository]
 */
class SQLiteShoppingListRepository(
    private val db: SQLiteDatabase,
    private val ioDispatcher: IoDispatcher
) : ShoppingListRepository {

    private var shoppingList: MutableList<ShoppingListRecipe> = mutableListOf()
    private var shoppingListLoaded = false
    private val shoppingListListeners = mutableSetOf<ShoppingListListener>()

    override suspend fun loadShoppingList(): MutableList<ShoppingListRecipe> = withContext(ioDispatcher.value) {
        delay(1000L)
        shoppingList = findShoppingList()
        shoppingListLoaded = true
        shoppingListNotifyChanges()
        return@withContext shoppingList
    }

    private fun findShoppingList(): MutableList<ShoppingListRecipe> {
        val shoppingList: MutableList<ShoppingListRecipe> = mutableListOf()
        val recipes = findRecipes()
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
        val cursor = queryShoppingListIngredients(recipeId)
        return cursor.use {
            val list = mutableListOf<ShoppingListRecipeIngredient>()
            while (cursor.moveToNext()) {
                list.add(parseShoppingListIngredient(cursor))
            }
            return@use list
        }
    }

    private fun parseShoppingListIngredient(cursor: Cursor): ShoppingListRecipeIngredient {
        return ShoppingListRecipeIngredient(
            ingredient = RecipeIngredient(
                id = cursor.getLong(cursor.getColumnIndexOrThrow("${RecipeIngredientsTable.TABLE_NAME}_${RecipeIngredientsTable.COLUMN_ID}")),
                product = cursor.getString(cursor.getColumnIndexOrThrow("${RecipeIngredientsTable.TABLE_NAME}_${RecipeIngredientsTable.COLUMN_NAME}")),
                amount = cursor.getString(cursor.getColumnIndexOrThrow("${RecipesIngredientsJoinTable.TABLE_NAME}_${RecipesIngredientsJoinTable.COLUMN_AMOUNT}"))
                    .toDoubleOrNull() ?: 0.0,
                measure = cursor.getString(cursor.getColumnIndexOrThrow("${IngredientMeasuresTable.TABLE_NAME}_${IngredientMeasuresTable.COLUMN_NAME}")),
                isInShoppingList = cursor.getInt(cursor.getColumnIndexOrThrow("${RecipesIngredientsJoinTable.TABLE_NAME}_${RecipesIngredientsJoinTable.COLUMN_IS_IN_SHOPPING_LIST}")) == 1,
            ),
            isSelectAndCross = false
        )
    }


    private fun queryShoppingListIngredients(recipeId: Long): Cursor {
        val sql = buildString {
            append("SELECT ")
            append("${RecipeIngredientsTable.TABLE_NAME}.${RecipeIngredientsTable.COLUMN_ID} AS ${RecipeIngredientsTable.TABLE_NAME}_${RecipeIngredientsTable.COLUMN_ID}, ")
            append("${RecipeIngredientsTable.TABLE_NAME}.${RecipeIngredientsTable.COLUMN_NAME} AS ${RecipeIngredientsTable.TABLE_NAME}_${RecipeIngredientsTable.COLUMN_NAME}, ")
            append("${RecipesIngredientsJoinTable.TABLE_NAME}.${RecipesIngredientsJoinTable.COLUMN_AMOUNT} AS ${RecipesIngredientsJoinTable.TABLE_NAME}_${RecipesIngredientsJoinTable.COLUMN_AMOUNT}, ")
            append("${IngredientMeasuresTable.TABLE_NAME}.${IngredientMeasuresTable.COLUMN_NAME} AS ${IngredientMeasuresTable.TABLE_NAME}_${IngredientMeasuresTable.COLUMN_NAME}, ")
            append("${RecipesIngredientsJoinTable.TABLE_NAME}.${RecipesIngredientsJoinTable.COLUMN_IS_IN_SHOPPING_LIST} AS ${RecipesIngredientsJoinTable.TABLE_NAME}_${RecipesIngredientsJoinTable.COLUMN_IS_IN_SHOPPING_LIST} ")
            append("FROM ")
            append("${RecipeIngredientsTable.TABLE_NAME} ")
            append("INNER JOIN ${RecipesIngredientsJoinTable.TABLE_NAME} ON ${RecipesIngredientsJoinTable.TABLE_NAME}.${RecipesIngredientsJoinTable.COLUMN_INGREDIENT_ID} = ${RecipeIngredientsTable.TABLE_NAME}.${RecipeIngredientsTable.COLUMN_ID} ")
            append("INNER JOIN ${RecipesTable.TABLE_NAME} ON ${RecipesTable.TABLE_NAME}.${RecipesTable.COLUMN_ID} = ${RecipesIngredientsJoinTable.TABLE_NAME}.${RecipesIngredientsJoinTable.COLUMN_RECIPE_ID} ")
            append("INNER JOIN ${RecipeIngredientsIngredientMeasuresJoinTable.TABLE_NAME} ON ${RecipeIngredientsIngredientMeasuresJoinTable.TABLE_NAME}.${RecipeIngredientsIngredientMeasuresJoinTable.COLUMN_RECIPE_INGREDIENT_ID} = ${RecipeIngredientsTable.TABLE_NAME}.${RecipeIngredientsTable.COLUMN_ID} ")
            append("INNER JOIN ${IngredientMeasuresTable.TABLE_NAME} ON ${IngredientMeasuresTable.TABLE_NAME}.${IngredientMeasuresTable.COLUMN_ID} = ${RecipeIngredientsIngredientMeasuresJoinTable.TABLE_NAME}.${RecipeIngredientsIngredientMeasuresJoinTable.COLUMN_INGREDIENT_MEASURE_ID} ")
            append("WHERE ${RecipesTable.TABLE_NAME}.${RecipesTable.COLUMN_ID} = $recipeId ")
            append("AND ")
            append("${RecipesIngredientsJoinTable.TABLE_NAME}_${RecipesIngredientsJoinTable.COLUMN_IS_IN_SHOPPING_LIST} = 1")
        }
        return db.rawQuery(sql, null)
    }

    private fun findRecipes(): List<Recipe> {
        val cursor = queryRecipes()
        return cursor.use {
            val list = mutableListOf<Recipe>()
            while (cursor.moveToNext()) {
                list.add(parseRecipe(cursor))
            }
            return@use list
        }
    }

    private fun parseRecipe(cursor: Cursor): Recipe {
        return Recipe(
            id = cursor.getLong(cursor.getColumnIndexOrThrow(RecipesTable.COLUMN_ID)),
            photo = cursor.getString(cursor.getColumnIndexOrThrow(RecipesTable.COLUMN_PHOTO)),
            name = cursor.getString(cursor.getColumnIndexOrThrow(RecipesTable.COLUMN_NAME)),
            categoryId = cursor.getLong(cursor.getColumnIndexOrThrow(RecipesTable.COLUMN_CATEGORY_ID))
        )
    }

    private fun queryRecipes(): Cursor {
        val sql = buildString {
            append("SELECT * FROM ")
            append("${RecipesTable.TABLE_NAME} ")
        }
        return db.rawQuery(sql, null)
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
        shoppingListNotifyChanges()
    }

    override fun deleteIngredient(recipeId: Long, ingredient: RecipeIngredient): Flow<Int> =
        flow {
            var progress = 0
            while (progress < 100) {
                progress += 2
                delay(30)
                emit(progress)
            }
            Log.d("MyLog", shoppingList[0].ingredients[0].ingredient.isInShoppingList.toString())
            deleteShoppingListIngredient(recipeId, ingredient.id)
            shoppingList = findShoppingList()
            Log.d("MyLog", shoppingList[0].ingredients[0].ingredient.isInShoppingList.toString())
            shoppingListLoaded = true
            shoppingListNotifyChanges()
        }.flowOn(ioDispatcher.value)

    private fun deleteShoppingListIngredient(recipeId: Long, ingredientId: Long) {
        val sql = buildString {
            append("UPDATE ")
            append("${RecipesIngredientsJoinTable.TABLE_NAME} ")
            append("SET ${RecipesIngredientsJoinTable.COLUMN_IS_IN_SHOPPING_LIST} = 0 ")
            append("WHERE ${RecipesIngredientsJoinTable.COLUMN_RECIPE_ID} = $recipeId ")
            append("AND ")
            append("${RecipesIngredientsJoinTable.COLUMN_INGREDIENT_ID} = $ingredientId")
        }
        db.execSQL(sql)
    }
}
