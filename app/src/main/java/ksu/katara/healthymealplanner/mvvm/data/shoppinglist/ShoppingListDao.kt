package ksu.katara.healthymealplanner.mvvm.data.shoppinglist

import androidx.room.Dao
import androidx.room.Query
import ksu.katara.healthymealplanner.mvvm.data.room.DatabaseContract
import ksu.katara.healthymealplanner.mvvm.data.shoppinglist.entities.ShoppingListTuples

@Dao
interface ShoppingListDao {

    @Query(
        "SELECT *FROM ${DatabaseContract.RecipesTable.TABLE_NAME} " +
                "INNER JOIN ${DatabaseContract.RecipesIngredientsJoinTable.TABLE_NAME} " +
                "ON ${DatabaseContract.RecipesIngredientsJoinTable.TABLE_NAME}.${DatabaseContract.RecipesIngredientsJoinTable.COLUMN_RECIPE_ID} " +
                "= ${DatabaseContract.RecipesTable.TABLE_NAME}.${DatabaseContract.RecipesTable.COLUMN_ID} " +
                "INNER JOIN ${DatabaseContract.RecipeIngredientsTable.TABLE_NAME} " +
                "ON ${DatabaseContract.RecipeIngredientsTable.TABLE_NAME}.${DatabaseContract.RecipeIngredientsTable.COLUMN_ID} " +
                "= ${DatabaseContract.RecipesIngredientsJoinTable.TABLE_NAME}.${DatabaseContract.RecipesIngredientsJoinTable.COLUMN_INGREDIENT_ID} " +
                "INNER JOIN ${DatabaseContract.RecipeIngredientsIngredientMeasuresJoinTable.TABLE_NAME} " +
                "ON ${DatabaseContract.RecipeIngredientsIngredientMeasuresJoinTable.TABLE_NAME}.${DatabaseContract.RecipeIngredientsIngredientMeasuresJoinTable.COLUMN_RECIPE_INGREDIENT_ID} " +
                "= ${DatabaseContract.RecipeIngredientsTable.TABLE_NAME}.${DatabaseContract.RecipeIngredientsTable.COLUMN_ID} " +
                "INNER JOIN ${DatabaseContract.IngredientMeasuresTable.TABLE_NAME} " +
                "ON ${DatabaseContract.IngredientMeasuresTable.TABLE_NAME}.${DatabaseContract.IngredientMeasuresTable.COLUMN_ID} " +
                "= ${DatabaseContract.RecipeIngredientsIngredientMeasuresJoinTable.TABLE_NAME}.${DatabaseContract.RecipeIngredientsIngredientMeasuresJoinTable.COLUMN_INGREDIENT_MEASURE_ID} " +
                "WHERE ${DatabaseContract.RecipesTable.TABLE_NAME}.${DatabaseContract.RecipesTable.COLUMN_ID} = :recipeId " +
                "AND " +
                "${DatabaseContract.RecipesIngredientsJoinTable.TABLE_NAME}.${DatabaseContract.RecipesIngredientsJoinTable.COLUMN_IS_IN_SHOPPING_LIST} = 1"
    )
    fun findShoppingListIngredients(recipeId: Long): List<ShoppingListTuples>

    @Query(
        "UPDATE ${DatabaseContract.RecipesIngredientsJoinTable.TABLE_NAME} " +
                "SET ${DatabaseContract.RecipesIngredientsJoinTable.COLUMN_IS_CROSS_IN_SHOPPING_LIST} = :isChecked " +
                "WHERE ${DatabaseContract.RecipesIngredientsJoinTable.COLUMN_RECIPE_ID} = :recipeId " +
                "AND " +
                "${DatabaseContract.RecipesIngredientsJoinTable.COLUMN_INGREDIENT_ID} = :ingredientId"
    )
    fun updateShoppingListIngredient(recipeId: Long, ingredientId: Long, isChecked: String)

    @Query(
        "UPDATE ${DatabaseContract.RecipesIngredientsJoinTable.TABLE_NAME} " +
                "SET ${DatabaseContract.RecipesIngredientsJoinTable.COLUMN_IS_IN_SHOPPING_LIST} = 0, " +
                "${DatabaseContract.RecipesIngredientsJoinTable.COLUMN_IS_CROSS_IN_SHOPPING_LIST} = 0 " +
                "WHERE ${DatabaseContract.RecipesIngredientsJoinTable.COLUMN_RECIPE_ID} = :recipeId " +
                "AND " +
                "${DatabaseContract.RecipesIngredientsJoinTable.COLUMN_INGREDIENT_ID} = :ingredientId"
    )
    fun deleteShoppingListIngredient(recipeId: Long, ingredientId: Long)

}