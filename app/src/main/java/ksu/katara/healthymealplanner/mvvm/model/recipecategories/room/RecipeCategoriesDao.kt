package ksu.katara.healthymealplanner.mvvm.model.recipecategories.room

import androidx.room.Dao
import androidx.room.Query
import ksu.katara.healthymealplanner.mvvm.model.recipecategories.room.entities.RecipeCategoryDBEntity
import ksu.katara.healthymealplanner.mvvm.model.sqlite.AppSQLiteContract.RecipeCategoriesTable

@Dao
interface RecipeCategoriesDao {

    @Query("SELECT * FROM ${RecipeCategoriesTable.TABLE_NAME}")
    fun loadRecipeCategories(): List<RecipeCategoryDBEntity>

    @Query("SELECT * FROM ${RecipeCategoriesTable.TABLE_NAME} WHERE ${RecipeCategoriesTable.COLUMN_ID} = :id")
    fun getRecipeCategoryById(id: Long): RecipeCategoryDBEntity

}