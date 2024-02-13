package ksu.katara.healthymealplanner.mvvm.model.shoppinglist.room

import androidx.room.Dao
import androidx.room.Query
import ksu.katara.healthymealplanner.mvvm.model.shoppinglist.room.entities.ShoppingListTuples
import ksu.katara.healthymealplanner.mvvm.model.sqlite.AppSQLiteContract

@Dao
interface ShoppingListDao {

    @Query(
        "SELECT *FROM ${AppSQLiteContract.RecipesTable.TABLE_NAME} " +
                "INNER JOIN ${AppSQLiteContract.RecipesIngredientsJoinTable.TABLE_NAME} " +
                "ON ${AppSQLiteContract.RecipesIngredientsJoinTable.TABLE_NAME}.${AppSQLiteContract.RecipesIngredientsJoinTable.COLUMN_RECIPE_ID} " +
                "= ${AppSQLiteContract.RecipesTable.TABLE_NAME}.${AppSQLiteContract.RecipesTable.COLUMN_ID} " +
                "INNER JOIN ${AppSQLiteContract.RecipeIngredientsTable.TABLE_NAME} " +
                "ON ${AppSQLiteContract.RecipeIngredientsTable.TABLE_NAME}.${AppSQLiteContract.RecipeIngredientsTable.COLUMN_ID} " +
                "= ${AppSQLiteContract.RecipesIngredientsJoinTable.TABLE_NAME}.${AppSQLiteContract.RecipesIngredientsJoinTable.COLUMN_INGREDIENT_ID} " +
                "INNER JOIN ${AppSQLiteContract.RecipeIngredientsIngredientMeasuresJoinTable.TABLE_NAME} " +
                "ON ${AppSQLiteContract.RecipeIngredientsIngredientMeasuresJoinTable.TABLE_NAME}.${AppSQLiteContract.RecipeIngredientsIngredientMeasuresJoinTable.COLUMN_RECIPE_INGREDIENT_ID} " +
                "= ${AppSQLiteContract.RecipeIngredientsTable.TABLE_NAME}.${AppSQLiteContract.RecipeIngredientsTable.COLUMN_ID} " +
                "INNER JOIN ${AppSQLiteContract.IngredientMeasuresTable.TABLE_NAME} " +
                "ON ${AppSQLiteContract.IngredientMeasuresTable.TABLE_NAME}.${AppSQLiteContract.IngredientMeasuresTable.COLUMN_ID} " +
                "= ${AppSQLiteContract.RecipeIngredientsIngredientMeasuresJoinTable.TABLE_NAME}.${AppSQLiteContract.RecipeIngredientsIngredientMeasuresJoinTable.COLUMN_INGREDIENT_MEASURE_ID} " +
                "WHERE ${AppSQLiteContract.RecipesTable.TABLE_NAME}.${AppSQLiteContract.RecipesTable.COLUMN_ID} = :recipeId " +
                "AND " +
                "${AppSQLiteContract.RecipesIngredientsJoinTable.TABLE_NAME}.${AppSQLiteContract.RecipesIngredientsJoinTable.COLUMN_IS_IN_SHOPPING_LIST} = 1"
    )
    fun findShoppingListIngredients(recipeId: Long): List<ShoppingListTuples>

    @Query(
        "UPDATE ${AppSQLiteContract.RecipesIngredientsJoinTable.TABLE_NAME} " +
                "SET ${AppSQLiteContract.RecipesIngredientsJoinTable.COLUMN_IS_CROSS_IN_SHOPPING_LIST} = :isChecked " +
                "WHERE ${AppSQLiteContract.RecipesIngredientsJoinTable.COLUMN_RECIPE_ID} = :recipeId " +
                "AND " +
                "${AppSQLiteContract.RecipesIngredientsJoinTable.COLUMN_INGREDIENT_ID} = :ingredientId"
    )
    fun updateShoppingListIngredient(recipeId: Long, ingredientId: Long, isChecked: String)

    @Query(
        "UPDATE ${AppSQLiteContract.RecipesIngredientsJoinTable.TABLE_NAME} " +
                "SET ${AppSQLiteContract.RecipesIngredientsJoinTable.COLUMN_IS_IN_SHOPPING_LIST} = 0, " +
                "${AppSQLiteContract.RecipesIngredientsJoinTable.COLUMN_IS_CROSS_IN_SHOPPING_LIST} = 0 " +
                "WHERE ${AppSQLiteContract.RecipesIngredientsJoinTable.COLUMN_RECIPE_ID} = :recipeId " +
                "AND " +
                "${AppSQLiteContract.RecipesIngredientsJoinTable.COLUMN_INGREDIENT_ID} = :ingredientId"
    )
    fun deleteShoppingListIngredient(recipeId: Long, ingredientId: Long)

}