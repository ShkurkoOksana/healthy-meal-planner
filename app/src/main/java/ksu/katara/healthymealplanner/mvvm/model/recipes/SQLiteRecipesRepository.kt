package ksu.katara.healthymealplanner.mvvm.model.recipes

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import ksu.katara.healthymealplanner.foundation.model.coroutines.IoDispatcher
import ksu.katara.healthymealplanner.mvvm.model.recipes.entities.Recipe
import ksu.katara.healthymealplanner.mvvm.model.recipes.entities.RecipeDetails
import ksu.katara.healthymealplanner.mvvm.model.recipes.entities.RecipeIngredient
import ksu.katara.healthymealplanner.mvvm.model.recipes.entities.RecipePreparationStep
import ksu.katara.healthymealplanner.mvvm.model.sqlite.AppSQLiteContract.IngredientMeasuresTable
import ksu.katara.healthymealplanner.mvvm.model.sqlite.AppSQLiteContract.RecipeCuisineTypesTable
import ksu.katara.healthymealplanner.mvvm.model.sqlite.AppSQLiteContract.RecipeIngredientsIngredientMeasuresJoinTable
import ksu.katara.healthymealplanner.mvvm.model.sqlite.AppSQLiteContract.RecipeIngredientsTable
import ksu.katara.healthymealplanner.mvvm.model.sqlite.AppSQLiteContract.RecipePreparationStepsTable
import ksu.katara.healthymealplanner.mvvm.model.sqlite.AppSQLiteContract.RecipeTypesTable
import ksu.katara.healthymealplanner.mvvm.model.sqlite.AppSQLiteContract.RecipesIngredientsJoinTable
import ksu.katara.healthymealplanner.mvvm.model.sqlite.AppSQLiteContract.RecipesPreparationStepsJoinTable
import ksu.katara.healthymealplanner.mvvm.model.sqlite.AppSQLiteContract.RecipesRecipeTypesJoinTable
import ksu.katara.healthymealplanner.mvvm.model.sqlite.AppSQLiteContract.RecipesTable

/**
 * Simple in-memory implementation of [RecipesRepository]
 */
class SQLiteRecipesRepository(
    private val db: SQLiteDatabase,
    private val ioDispatcher: IoDispatcher
) : RecipesRepository {

    private lateinit var recipeDetails: RecipeDetails
    private var recipeDetailsLoaded = false
    private val recipeDetailsListeners = mutableSetOf<RecipeDetailsListener>()

    private var types = listOf<String>()
    private var typesLoaded = false

    private var recipesInCategory = mutableListOf<Recipe>()
    private var recipesInCategoryLoaded = false
    private val recipesInCategoryListeners = mutableSetOf<RecipesInCategoryListener>()

    private var ingredients = mutableListOf<RecipeIngredient>()
    private var ingredientsLoaded = false
    private val ingredientsListeners = mutableSetOf<RecipeIngredientsListener>()

    private var preparationSteps = mutableListOf<RecipePreparationStep>()
    private var preparationStepsLoaded = false

    override suspend fun loadRecipesInCategory(recipeCategoryId: Long): List<Recipe> =
        withContext(ioDispatcher.value) {
            delay(1000L)
            recipesInCategory = findRecipesInCategory(recipeCategoryId)
            recipesInCategoryLoaded = true
            notifyRecipeInCategoryChanges()
            return@withContext recipesInCategory
        }

    private fun findRecipesInCategory(recipeCategoryId: Long): MutableList<Recipe> {
        val cursor = queryRecipesInCategory(recipeCategoryId)
        return cursor.use {
            val list = mutableListOf<Recipe>()
            while (cursor.moveToNext()) {
                list.add(parseRecipe(cursor))
            }
            return@use list
        }
    }

    private fun queryRecipesInCategory(recipeCategoryId: Long): Cursor {
        val sql = "SELECT * FROM ${RecipesTable.TABLE_NAME} WHERE ${RecipesTable.TABLE_NAME}.${RecipesTable.COLUMN_CATEGORY_ID} == $recipeCategoryId"
        return db.rawQuery(sql, null)
    }

    override fun addRecipeInCategoryListener(listener: RecipesInCategoryListener) {
        recipesInCategoryListeners.add(listener)
        if (recipesInCategoryLoaded) {
            listener.invoke(recipesInCategory)
        }
    }

    override fun removeRecipeInCategoryListener(listener: RecipesInCategoryListener) {
        recipesInCategoryListeners.remove(listener)
    }

    private fun notifyRecipeInCategoryChanges() {
        if (!recipesInCategoryLoaded) return
        recipesInCategoryListeners.forEach { it.invoke(recipesInCategory) }
    }

    override suspend fun loadRecipeDetails(recipeId: Long): RecipeDetails  =
        withContext(ioDispatcher.value) {
        delay(1000L)
        recipeDetails = findRecipeDetails(recipeId)
        recipeDetailsLoaded = true
        notifyRecipeDetailsChanges()
        return@withContext recipeDetails
        }

    private fun findRecipeDetails(recipeId: Long): RecipeDetails {
        return RecipeDetails(
            recipe = findRecipeById(recipeId),
            preparationTime = 30,
            cuisineType = findRecipeCuisineTypeByRecipeId(recipeId),
            types = mutableListOf(),
            energeticValue = findRecipeEnergeticValueByRecipeId(recipeId),
            proteins = findRecipeProteins(recipeId),
            fats = findRecipeFats(recipeId),
            carbohydrates = findRecipeCarbohydrates(recipeId),
            ingredients = mutableListOf(),
            preparationSteps = mutableListOf(),
            isFavorite = findIsRecipeFavorite(recipeId)
        )
    }
    private fun findIsRecipeFavorite(recipeId: Long): Boolean {
        val cursor = queryRecipe(recipeId)
        cursor.use {
            cursor.moveToFirst()
            return parseIsRecipeFavorite(cursor)
        }
    }

    private fun parseIsRecipeFavorite(cursor: Cursor): Boolean {
        return cursor.getInt(cursor.getColumnIndexOrThrow(RecipesTable.COLUMN_IS_FAVORITE)) == 1
    }

    private fun findRecipeCarbohydrates(recipeId: Long): Int {
        val cursor = queryRecipe(recipeId)
        cursor.use {
            cursor.moveToFirst()
            return parseRecipeCarbohydrates(cursor)
        }
    }

    private fun parseRecipeCarbohydrates(cursor: Cursor): Int {
        return cursor.getInt(cursor.getColumnIndexOrThrow(RecipesTable.COLUMN_CARBOHYDRATES))
    }

    private fun findRecipeFats(recipeId: Long): Int {
        val cursor = queryRecipe(recipeId)
        cursor.use {
            cursor.moveToFirst()
            return parseRecipeFats(cursor)
        }
    }

    private fun parseRecipeFats(cursor: Cursor): Int {
        return cursor.getInt(cursor.getColumnIndexOrThrow(RecipesTable.COLUMN_FATS))
    }

    private fun findRecipeProteins(recipeId: Long): Int {
        val cursor = queryRecipe(recipeId)
        cursor.use {
            cursor.moveToFirst()
            return parseRecipeProteins(cursor)
        }
    }

    private fun parseRecipeProteins(cursor: Cursor): Int {
        return cursor.getInt(cursor.getColumnIndexOrThrow(RecipesTable.COLUMN_PROTEINS))
    }

    private fun findRecipeEnergeticValueByRecipeId(recipeId: Long): Int {
        val cursor = queryRecipe(recipeId)
        cursor.use {
            cursor.moveToFirst()
            return parseRecipeEnergeticValue(cursor)
        }
    }

    private fun parseRecipeEnergeticValue(cursor: Cursor): Int {
        return cursor.getInt(cursor.getColumnIndexOrThrow(RecipesTable.COLUMN_ENERGETIC_VALUE))
    }

    private fun queryRecipe(recipeId: Long): Cursor {
        val sql = buildString {
            append("SELECT * ")
            append("FROM ")
            append("${RecipesTable.TABLE_NAME} ")
            append("WHERE ${RecipesTable.TABLE_NAME}.${RecipesTable.COLUMN_ID} = $recipeId")
        }
        return db.rawQuery(sql, null)
    }

    private fun findRecipeCuisineTypeByRecipeId(recipeId: Long): String {
        val cursor = queryRecipeCuisineType(recipeId)
        cursor.use {
            cursor.moveToFirst()
            return parseRecipeCuisineType(cursor)
        }
    }

    private fun parseRecipeCuisineType(cursor: Cursor): String {
        return cursor.getString(cursor.getColumnIndexOrThrow("${RecipeCuisineTypesTable.TABLE_NAME}_${RecipeCuisineTypesTable.COLUMN_NAME}"))
    }

    private fun queryRecipeCuisineType(recipeId: Long): Cursor {
        val sql = buildString {
            append("SELECT ")
            append("${RecipeCuisineTypesTable.TABLE_NAME}.${RecipeCuisineTypesTable.COLUMN_NAME} AS ${RecipeCuisineTypesTable.TABLE_NAME}_${RecipeCuisineTypesTable.COLUMN_NAME} ")
            append("FROM ")
            append("${RecipeCuisineTypesTable.TABLE_NAME} ")
            append("INNER JOIN ${RecipesTable.TABLE_NAME} ON ${RecipesTable.TABLE_NAME}.${RecipesTable.COLUMN_CUISINE_TYPE_ID} = ${RecipeCuisineTypesTable.TABLE_NAME}.${RecipeCuisineTypesTable.COLUMN_ID} ")
            append("WHERE ${RecipesTable.TABLE_NAME}.${RecipesTable.COLUMN_ID} = $recipeId")
        }
        return db.rawQuery(sql, null)
    }

    private fun findRecipeById(recipeId: Long): Recipe {
        val cursor = queryRecipeById(recipeId)
        cursor.moveToFirst()
        return parseRecipe(cursor)
    }

    private fun parseRecipe(cursor: Cursor): Recipe {
        return Recipe(
            id = cursor.getLong(cursor.getColumnIndexOrThrow(RecipesTable.COLUMN_ID)),
            photo = cursor.getString(cursor.getColumnIndexOrThrow(RecipesTable.COLUMN_PHOTO)),
            name = cursor.getString(cursor.getColumnIndexOrThrow(RecipesTable.COLUMN_NAME)),
            categoryId = cursor.getLong(cursor.getColumnIndexOrThrow(RecipesTable.COLUMN_CATEGORY_ID))
        )
    }

    private fun queryRecipeById(recipeId: Long): Cursor {
        val sql = buildString {
            append("SELECT * FROM ")
            append("${RecipesTable.TABLE_NAME} ")
            append("WHERE ${RecipesTable.TABLE_NAME}.${RecipesTable.COLUMN_ID} == $recipeId")
        }
        return db.rawQuery(sql, null)
    }

    override fun addRecipeDetailsListener(listener: RecipeDetailsListener) {
        recipeDetailsListeners.add(listener)
        if (recipeDetailsLoaded) {
            listener.invoke(recipeDetails)
        }
    }

    override fun removeRecipeDetailsListener(listener: RecipeDetailsListener) {
        recipeDetailsListeners.remove(listener)
    }

    private fun notifyRecipeDetailsChanges() {
        if (!recipeDetailsLoaded) return
        recipeDetailsListeners.forEach { it.invoke(recipeDetails) }
    }

    override suspend fun loadRecipeTypes(recipeId: Long): List<String> =
        withContext(ioDispatcher.value) {
            delay(1000L)
            types = findRecipeTypesByRecipeId(recipeId)
            typesLoaded = true
            return@withContext types
        }

    private fun findRecipeTypesByRecipeId(recipeId: Long): MutableList<String> {
        val cursor = queryRecipeTypes(recipeId)
        return cursor.use {
            val list = mutableListOf<String>()
            while (cursor.moveToNext()) {
                list.add(parseRecipeType(cursor))
            }
            return@use list
        }
    }

    private fun parseRecipeType(cursor: Cursor): String {
        return cursor.getString(cursor.getColumnIndexOrThrow("${RecipeTypesTable.TABLE_NAME}_${RecipeTypesTable.COLUMN_NAME}"))
    }

    private fun queryRecipeTypes(recipeId: Long): Cursor {
        val sql = buildString {
            append("SELECT ")
            append("${RecipeTypesTable.TABLE_NAME}.${RecipeTypesTable.COLUMN_NAME} AS ${RecipeTypesTable.TABLE_NAME}_${RecipeTypesTable.COLUMN_NAME} ")
            append("FROM ")
            append("${RecipeTypesTable.TABLE_NAME} ")
            append("INNER JOIN ${RecipesRecipeTypesJoinTable.TABLE_NAME} ON ${RecipesRecipeTypesJoinTable.TABLE_NAME}.${RecipesRecipeTypesJoinTable.COLUMN_RECIPE_TYPE_ID} = ${RecipeTypesTable.TABLE_NAME}.${RecipeTypesTable.COLUMN_ID} ")
            append("INNER JOIN ${RecipesTable.TABLE_NAME} ON ${RecipesTable.TABLE_NAME}.${RecipesTable.COLUMN_ID} = ${RecipesRecipeTypesJoinTable.TABLE_NAME}.${RecipesRecipeTypesJoinTable.COLUMN_RECIPE_ID} ")
            append("WHERE ${RecipesTable.TABLE_NAME}.${RecipesTable.COLUMN_ID} = $recipeId")
        }
        return db.rawQuery(sql, null)
    }

    override suspend fun loadIngredients(recipeId: Long): List<RecipeIngredient> =
        withContext(ioDispatcher.value) {
            delay(1000L)
            ingredients = findRecipeIngredients(recipeId)
            ingredientsLoaded = true
            notifyIngredientsChanges()
            return@withContext ingredients
        }

    private fun findRecipeIngredients(recipeId: Long): MutableList<RecipeIngredient> {
        val cursor = queryRecipeIngredients(recipeId)
        return cursor.use {
            val list = mutableListOf<RecipeIngredient>()
            while (cursor.moveToNext()) {
                list.add(parseRecipeIngredient(cursor))
            }
            return@use list
        }
    }

    private fun parseRecipeIngredient(cursor: Cursor): RecipeIngredient {
        return RecipeIngredient(
            id = cursor.getLong(cursor.getColumnIndexOrThrow("${RecipeIngredientsTable.TABLE_NAME}_${RecipeIngredientsTable.COLUMN_ID}")),
            product = cursor.getString(cursor.getColumnIndexOrThrow("${RecipeIngredientsTable.TABLE_NAME}_${RecipeIngredientsTable.COLUMN_NAME}")),
            amount = cursor.getString(cursor.getColumnIndexOrThrow("${RecipesIngredientsJoinTable.TABLE_NAME}_${RecipesIngredientsJoinTable.COLUMN_AMOUNT}"))
                .toDoubleOrNull() ?: 0.0,
            measure = cursor.getString(cursor.getColumnIndexOrThrow("${IngredientMeasuresTable.TABLE_NAME}_${IngredientMeasuresTable.COLUMN_NAME}")),
            isInShoppingList = cursor.getInt(cursor.getColumnIndexOrThrow("${RecipesIngredientsJoinTable.TABLE_NAME}_${RecipesIngredientsJoinTable.COLUMN_IS_IN_SHOPPING_LIST}")) == 1
        )
    }

    private fun queryRecipeIngredients(recipeId: Long): Cursor {
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
            append("WHERE ${RecipesTable.TABLE_NAME}.${RecipesTable.COLUMN_ID} = $recipeId")
        }
        return db.rawQuery(sql, null)
    }

    override fun addIngredientListener(listener: RecipeIngredientsListener) {
        ingredientsListeners.add(listener)
        if (ingredientsLoaded) {
            listener.invoke(ingredients)
        }
    }

    override fun removeIngredientsListener(listener: RecipeIngredientsListener) {
        ingredientsListeners.remove(listener)
    }

    private fun notifyIngredientsChanges() {
        if (!ingredientsLoaded) return
        ingredientsListeners.forEach { it.invoke(ingredients) }
    }

    override suspend fun setIngredientSelected(
        recipeId: Long,
        ingredientId: Long,
        isSelected: Boolean
    ): Unit = withContext(ioDispatcher.value) {
        delay(1000L)
        updateRecipeIngredient(recipeId, ingredientId, isSelected)
        ingredients = findRecipeIngredients(recipeId)
        ingredientsLoaded = true
        notifyIngredientsChanges()
    }

    private fun updateRecipeIngredient(recipeId: Long, ingredientId: Long, selected: Boolean) {
        val sql = buildString {
            append("UPDATE ${RecipesIngredientsJoinTable.TABLE_NAME} ")
            append("SET ${RecipesIngredientsJoinTable.COLUMN_IS_IN_SHOPPING_LIST} = $selected ")
            append("WHERE ${RecipesIngredientsJoinTable.COLUMN_RECIPE_ID} = $recipeId ")
            append("AND ")
            append("${RecipesIngredientsJoinTable.COLUMN_INGREDIENT_ID} = $ingredientId")
        }
        db.execSQL(sql)
    }

    override suspend fun setAllIngredientsSelected(recipeId: Long, isSelected: Boolean) =
        withContext(ioDispatcher.value) {
            delay(1000L)
            updateAllRecipeIngredients(recipeId, isSelected)
            ingredients = findRecipeIngredients(recipeId)
            ingredientsLoaded = true
            notifyIngredientsChanges()
        }

    private fun updateAllRecipeIngredients(recipeId: Long, selected: Boolean) {
        val sql = buildString {
            append("UPDATE ${RecipesIngredientsJoinTable.TABLE_NAME} ")
            append("SET ${RecipesIngredientsJoinTable.COLUMN_IS_IN_SHOPPING_LIST} = $selected ")
            append("WHERE ${RecipesIngredientsJoinTable.COLUMN_RECIPE_ID} = $recipeId ")
        }
        db.execSQL(sql)
    }

    override suspend fun isAllIngredientsSelected(recipeId: Long): Boolean =
        withContext(ioDispatcher.value) {
            delay(1000L)
            return@withContext findIsAllIngredientsSelected(recipeId)
        }

    private fun findIsAllIngredientsSelected(recipeId: Long): Boolean {
        val cursor = queryIsAllIngredientsSelected(recipeId)
        var result = 0
        if (cursor.moveToFirst()) {
            result = cursor.getInt(cursor.getColumnIndexOrThrow(RecipesIngredientsJoinTable.COLUMN_TOTAL_SUM_IS_IN_SHOPPING_LIST))
        }
        return result == ingredients.size
    }

    private fun queryIsAllIngredientsSelected(recipeId: Long): Cursor {
        val sql = buildString {
            append("SELECT ")
            append("SUM(${RecipesIngredientsJoinTable.COLUMN_IS_IN_SHOPPING_LIST}) AS ${RecipesIngredientsJoinTable.COLUMN_TOTAL_SUM_IS_IN_SHOPPING_LIST} ")
            append("FROM ${RecipesIngredientsJoinTable.TABLE_NAME} ")
            append("WHERE ${RecipesIngredientsJoinTable.COLUMN_RECIPE_ID} = $recipeId ")
        }
        return db.rawQuery(sql, null)
    }

    override suspend fun loadPreparationSteps(recipeId: Long): List<RecipePreparationStep> =
        withContext(ioDispatcher.value) {
            delay(1000L)
            preparationSteps = findRecipePreparationSteps(recipeId)
            preparationStepsLoaded = true
            return@withContext preparationSteps
        }

    private fun findRecipePreparationSteps(recipeId: Long): MutableList<RecipePreparationStep> {
        val cursor = queryRecipePreparationSteps(recipeId)
        return cursor.use {
            val list = mutableListOf<RecipePreparationStep>()
            while (cursor.moveToNext()) {
                list.add(parseRecipePreparationSteps(cursor))
            }
            return@use list
        }
    }
    private fun parseRecipePreparationSteps(cursor: Cursor): RecipePreparationStep {
        return RecipePreparationStep(
            id = cursor.getLong(cursor.getColumnIndexOrThrow("${RecipePreparationStepsTable.TABLE_NAME}_${RecipePreparationStepsTable.COLUMN_ID}")),
            step = cursor.getInt(cursor.getColumnIndexOrThrow("${RecipePreparationStepsTable.TABLE_NAME}_${RecipePreparationStepsTable.COLUMN_ID}")),
            photo = cursor.getString(cursor.getColumnIndexOrThrow("${RecipePreparationStepsTable.TABLE_NAME}_${RecipePreparationStepsTable.COLUMN_PHOTO}")),
            description = cursor.getString(cursor.getColumnIndexOrThrow("${RecipePreparationStepsTable.TABLE_NAME}_${RecipePreparationStepsTable.COLUMN_DESCRIPTION}"))
        )
    }

    private fun queryRecipePreparationSteps(recipeId: Long): Cursor {
        val sql = buildString {
            append("SELECT ")
            append("${RecipePreparationStepsTable.TABLE_NAME}.${RecipePreparationStepsTable.COLUMN_ID} AS ${RecipePreparationStepsTable.TABLE_NAME}_${RecipePreparationStepsTable.COLUMN_ID}, ")
            append("${RecipePreparationStepsTable.TABLE_NAME}.${RecipePreparationStepsTable.COLUMN_PHOTO} AS ${RecipePreparationStepsTable.TABLE_NAME}_${RecipePreparationStepsTable.COLUMN_PHOTO}, ")
            append("${RecipePreparationStepsTable.TABLE_NAME}.${RecipePreparationStepsTable.COLUMN_DESCRIPTION} AS ${RecipePreparationStepsTable.TABLE_NAME}_${RecipePreparationStepsTable.COLUMN_DESCRIPTION} ")
            append("FROM ")
            append("${RecipePreparationStepsTable.TABLE_NAME} ")
            append("INNER JOIN ${RecipesPreparationStepsJoinTable.TABLE_NAME} ON ${RecipesPreparationStepsJoinTable.TABLE_NAME}.${RecipesPreparationStepsJoinTable.COLUMN_PREPARATION_STEP_ID} = ${RecipePreparationStepsTable.TABLE_NAME}.${RecipePreparationStepsTable.COLUMN_ID} ")
            append("INNER JOIN ${RecipesTable.TABLE_NAME} ON ${RecipesTable.TABLE_NAME}.${RecipesTable.COLUMN_ID} = ${RecipesPreparationStepsJoinTable.TABLE_NAME}.${RecipesPreparationStepsJoinTable.COLUMN_RECIPE_ID} ")
            append("WHERE ${RecipesTable.TABLE_NAME}.${RecipesTable.COLUMN_ID} = $recipeId")
        }
        return db.rawQuery(sql, null)
    }

}