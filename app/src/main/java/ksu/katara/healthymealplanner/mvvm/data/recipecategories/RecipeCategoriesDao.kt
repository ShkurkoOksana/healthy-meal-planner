package ksu.katara.healthymealplanner.mvvm.data.recipecategories

import androidx.room.Dao
import androidx.room.Query
import ksu.katara.healthymealplanner.mvvm.data.recipecategories.entities.RecipeCategoryDBEntity
import ksu.katara.healthymealplanner.mvvm.data.room.DatabaseContract.RecipeCategoriesTable

@Dao
interface RecipeCategoriesDao {

    @Query("SELECT * FROM ${RecipeCategoriesTable.TABLE_NAME}")
    fun loadRecipeCategories(): List<RecipeCategoryDBEntity>

    @Query("SELECT * FROM ${RecipeCategoriesTable.TABLE_NAME} WHERE ${RecipeCategoriesTable.COLUMN_ID} = :id")
    fun getRecipeCategoryById(id: Long): RecipeCategoryDBEntity

}