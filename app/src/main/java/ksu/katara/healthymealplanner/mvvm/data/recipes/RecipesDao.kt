package ksu.katara.healthymealplanner.mvvm.data.recipes

import androidx.room.Dao
import androidx.room.Query
import ksu.katara.healthymealplanner.mvvm.data.recipes.entities.RecipeDBEntity
import ksu.katara.healthymealplanner.mvvm.data.recipes.entities.RecipesAndCuisineTypesTuple
import ksu.katara.healthymealplanner.mvvm.data.recipes.entities.RecipesAndIngredientsTuple
import ksu.katara.healthymealplanner.mvvm.data.recipes.entities.RecipesAndPreparationStepsTuple
import ksu.katara.healthymealplanner.mvvm.data.recipes.entities.RecipesAndRecipeTypesTuple
import ksu.katara.healthymealplanner.mvvm.data.room.DatabaseContract.IngredientMeasuresTable
import ksu.katara.healthymealplanner.mvvm.data.room.DatabaseContract.RecipeCuisineTypesTable
import ksu.katara.healthymealplanner.mvvm.data.room.DatabaseContract.RecipeIngredientsIngredientMeasuresJoinTable
import ksu.katara.healthymealplanner.mvvm.data.room.DatabaseContract.RecipeIngredientsTable
import ksu.katara.healthymealplanner.mvvm.data.room.DatabaseContract.RecipePreparationStepsTable
import ksu.katara.healthymealplanner.mvvm.data.room.DatabaseContract.RecipeTypesTable
import ksu.katara.healthymealplanner.mvvm.data.room.DatabaseContract.RecipesIngredientsJoinTable
import ksu.katara.healthymealplanner.mvvm.data.room.DatabaseContract.RecipesPreparationStepsJoinTable
import ksu.katara.healthymealplanner.mvvm.data.room.DatabaseContract.RecipesRecipeTypesJoinTable
import ksu.katara.healthymealplanner.mvvm.data.room.DatabaseContract.RecipesTable

@Dao
interface RecipesDao {

    @Query(
        "SELECT * FROM ${RecipesTable.TABLE_NAME} " +
                "WHERE ${RecipesTable.TABLE_NAME}.${RecipesTable.COLUMN_CATEGORY_ID} == :id"
    )
    fun findRecipesInCategory(id: Long): List<RecipeDBEntity>

    @Query(
        "SELECT * FROM ${RecipesTable.TABLE_NAME} " +
                "WHERE ${RecipesTable.TABLE_NAME}.${RecipesTable.COLUMN_ID} == :id"
    )
    fun findRecipeById(id: Long): RecipeDBEntity

    @Query("SELECT * FROM ${RecipesTable.TABLE_NAME}")
    fun findRecipes(): List<RecipeDBEntity>

    @Query(
        "SELECT * " +
                "FROM ${RecipesTable.TABLE_NAME} " +
                "INNER JOIN ${RecipeCuisineTypesTable.TABLE_NAME} " +
                "ON ${RecipesTable.TABLE_NAME}.${RecipesTable.COLUMN_CUISINE_TYPE_ID} " +
                "= ${RecipeCuisineTypesTable.TABLE_NAME}.${RecipeCuisineTypesTable.COLUMN_ID} " +
                "WHERE ${RecipesTable.TABLE_NAME}.${RecipesTable.COLUMN_ID} = :id"
    )
    fun findRecipeCuisineTypeByRecipeId(id: Long): List<RecipesAndCuisineTypesTuple>

    @Query(
        "SELECT * FROM ${RecipesTable.TABLE_NAME} " +
                "INNER JOIN ${RecipesRecipeTypesJoinTable.TABLE_NAME} ON " +
                "${RecipesRecipeTypesJoinTable.TABLE_NAME}.${RecipesRecipeTypesJoinTable.COLUMN_RECIPE_ID} " +
                "= ${RecipesTable.TABLE_NAME}.${RecipesTable.COLUMN_ID} " +
                "INNER JOIN ${RecipeTypesTable.TABLE_NAME} ON " +
                "${RecipeTypesTable.TABLE_NAME}.${RecipeTypesTable.COLUMN_ID} " +
                "= ${RecipesRecipeTypesJoinTable.TABLE_NAME}.${RecipesRecipeTypesJoinTable.COLUMN_RECIPE_TYPE_ID} " +
                "WHERE ${RecipesTable.TABLE_NAME}.${RecipesTable.COLUMN_ID} = :id"
    )
    fun findRecipeTypesByRecipeId(id: Long): List<RecipesAndRecipeTypesTuple>

    @Query(
        "SELECT * FROM ${RecipesTable.TABLE_NAME} " +
                "INNER JOIN ${RecipesIngredientsJoinTable.TABLE_NAME} " +
                "ON ${RecipesIngredientsJoinTable.TABLE_NAME}.${RecipesIngredientsJoinTable.COLUMN_RECIPE_ID} " +
                "= ${RecipesTable.TABLE_NAME}.${RecipesTable.COLUMN_ID} " +
                "INNER JOIN ${RecipeIngredientsTable.TABLE_NAME} " +
                "ON ${RecipeIngredientsTable.TABLE_NAME}.${RecipeIngredientsTable.COLUMN_ID} " +
                "= ${RecipesIngredientsJoinTable.TABLE_NAME}.${RecipesIngredientsJoinTable.COLUMN_INGREDIENT_ID} " +
                "INNER JOIN ${RecipeIngredientsIngredientMeasuresJoinTable.TABLE_NAME} " +
                "ON ${RecipeIngredientsIngredientMeasuresJoinTable.TABLE_NAME}.${RecipeIngredientsIngredientMeasuresJoinTable.COLUMN_RECIPE_INGREDIENT_ID} " +
                "= ${RecipeIngredientsTable.TABLE_NAME}.${RecipeIngredientsTable.COLUMN_ID} " +
                "INNER JOIN ${IngredientMeasuresTable.TABLE_NAME} " +
                "ON ${IngredientMeasuresTable.TABLE_NAME}.${IngredientMeasuresTable.COLUMN_ID} " +
                "= ${RecipeIngredientsIngredientMeasuresJoinTable.TABLE_NAME}.${RecipeIngredientsIngredientMeasuresJoinTable.COLUMN_INGREDIENT_MEASURE_ID} " +
                "WHERE ${RecipesTable.TABLE_NAME}.${RecipesTable.COLUMN_ID} = :id"
    )
    fun findRecipeIngredientsByRecipeId(id: Long): List<RecipesAndIngredientsTuple>

    fun setIngredientSelected(
        recipeId: Long,
        ingredientId: Long,
        isSelected: Boolean
    ) {
    }

    fun setAllIngredientsSelected(recipeId: Long, isSelected: Boolean) {}

    @Query(
        "SELECT SUM(${RecipesIngredientsJoinTable.COLUMN_IS_IN_SHOPPING_LIST}) AS ${RecipesIngredientsJoinTable.COLUMN_TOTAL_SUM_IS_IN_SHOPPING_LIST} " +
                "FROM ${RecipesIngredientsJoinTable.TABLE_NAME} " +
                "WHERE ${RecipesIngredientsJoinTable.COLUMN_RECIPE_ID} = :id "
    )
    fun isAllIngredientsSelected(id: Long): Int

    @Query(
        "SELECT * FROM ${RecipesTable.TABLE_NAME} " +
                "INNER JOIN ${RecipesPreparationStepsJoinTable.TABLE_NAME} " +
                "ON ${RecipesPreparationStepsJoinTable.TABLE_NAME}.${RecipesPreparationStepsJoinTable.COLUMN_RECIPE_ID} " +
                "= ${RecipesTable.TABLE_NAME}.${RecipesTable.COLUMN_ID} " +
                "INNER JOIN ${RecipePreparationStepsTable.TABLE_NAME} " +
                "ON ${RecipePreparationStepsTable.TABLE_NAME}.${RecipePreparationStepsTable.COLUMN_ID} " +
                "= ${RecipesPreparationStepsJoinTable.TABLE_NAME}.${RecipesPreparationStepsJoinTable.COLUMN_PREPARATION_STEP_ID} " +
                "WHERE ${RecipesTable.TABLE_NAME}.${RecipesTable.COLUMN_ID} = :recipeId"
    )
    fun findRecipePreparationSteps(recipeId: Long): List<RecipesAndPreparationStepsTuple>

    @Query(
        "UPDATE ${RecipesIngredientsJoinTable.TABLE_NAME} " +
                "SET ${RecipesIngredientsJoinTable.COLUMN_IS_IN_SHOPPING_LIST} = :selected " +
                "WHERE ${RecipesIngredientsJoinTable.COLUMN_RECIPE_ID} = :recipeId " +
                "AND " +
                "${RecipesIngredientsJoinTable.COLUMN_INGREDIENT_ID} = :ingredientId"
    )
    fun updateRecipeIngredient(recipeId: Long, ingredientId: Long, selected: Int)

    @Query(
        "UPDATE ${RecipesIngredientsJoinTable.TABLE_NAME} " +
                "SET ${RecipesIngredientsJoinTable.COLUMN_IS_IN_SHOPPING_LIST} = :selected " +
                "WHERE ${RecipesIngredientsJoinTable.COLUMN_RECIPE_ID} = :recipeId "
    )
    fun updateAllRecipeIngredients(recipeId: Long, selected: Int)

}