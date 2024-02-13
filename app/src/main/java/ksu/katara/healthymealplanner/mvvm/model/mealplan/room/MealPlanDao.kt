package ksu.katara.healthymealplanner.mvvm.model.mealplan.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import ksu.katara.healthymealplanner.mvvm.model.mealplan.room.entities.MealPlanAndRecipesTuple
import ksu.katara.healthymealplanner.mvvm.model.mealplan.room.entities.MealPlanDBEntity
import ksu.katara.healthymealplanner.mvvm.model.mealplan.room.entities.MealPlanRecipesJoinTableDBEntity
import ksu.katara.healthymealplanner.mvvm.model.sqlite.AppSQLiteContract.MealPlanRecipesJoinTable
import ksu.katara.healthymealplanner.mvvm.model.sqlite.AppSQLiteContract.MealPlanTable
import ksu.katara.healthymealplanner.mvvm.model.sqlite.AppSQLiteContract.MealTypesTable
import ksu.katara.healthymealplanner.mvvm.model.sqlite.AppSQLiteContract.RecipesTable

@Dao
interface MealPlanDao {

    @Query(
        "SELECT * FROM ${RecipesTable.TABLE_NAME} " +
                "INNER JOIN ${MealPlanRecipesJoinTable.TABLE_NAME} " +
                "ON ${MealPlanRecipesJoinTable.TABLE_NAME}.${MealPlanRecipesJoinTable.COLUMN_RECIPE_ID} " +
                "= ${RecipesTable.TABLE_NAME}.${RecipesTable.COLUMN_ID} " +
                "INNER JOIN ${MealPlanTable.TABLE_NAME} " +
                "ON ${MealPlanTable.TABLE_NAME}.${MealPlanTable.COLUMN_ID} " +
                "= ${MealPlanRecipesJoinTable.TABLE_NAME}.${MealPlanRecipesJoinTable.COLUMN_MEAL_PLAN_ID} " +
                "INNER JOIN ${MealTypesTable.TABLE_NAME} " +
                "ON ${MealTypesTable.TABLE_NAME}.${MealTypesTable.COLUMN_ID} " +
                "= ${MealPlanRecipesJoinTable.TABLE_NAME}.${MealPlanRecipesJoinTable.COLUMN_MEAL_TYPE_ID} " +
                "WHERE ${MealTypesTable.TABLE_NAME}.${MealTypesTable.COLUMN_NAME} = :mealType " +
                "AND " +
                "${MealPlanTable.TABLE_NAME}.${MealPlanTable.COLUMN_DATE} = :selectedDate"
    )
    fun findMealPlanRecipes(selectedDate: String, mealType: String): List<MealPlanAndRecipesTuple>

    @Insert
    fun insertMealPlan(mealPlanDBEntity: MealPlanDBEntity): Long

    @Insert
    fun insertRecipeToMealPlan(mealPlanRecipesJoinTableDBEntity: MealPlanRecipesJoinTableDBEntity)

    @Query(
        "DELETE FROM ${MealPlanRecipesJoinTable.TABLE_NAME} " +
                "WHERE ${MealPlanRecipesJoinTable.TABLE_NAME}.${MealPlanRecipesJoinTable.COLUMN_RECIPE_ID} IN (" +
                "SELECT ${MealPlanRecipesJoinTable.TABLE_NAME}.${MealPlanRecipesJoinTable.COLUMN_RECIPE_ID} " +
                "FROM ${MealPlanRecipesJoinTable.TABLE_NAME} " +
                "INNER JOIN ${MealPlanTable.TABLE_NAME} ON ${MealPlanTable.TABLE_NAME}.${MealPlanTable.COLUMN_ID} = ${MealPlanRecipesJoinTable.TABLE_NAME}.${MealPlanRecipesJoinTable.COLUMN_MEAL_PLAN_ID} " +
                "INNER JOIN ${RecipesTable.TABLE_NAME} ON ${RecipesTable.TABLE_NAME}.${RecipesTable.COLUMN_ID} = ${MealPlanRecipesJoinTable.TABLE_NAME}.${MealPlanRecipesJoinTable.COLUMN_RECIPE_ID} " +
                "INNER JOIN ${MealTypesTable.TABLE_NAME} ON ${MealTypesTable.TABLE_NAME}.${MealTypesTable.COLUMN_ID} = ${MealPlanRecipesJoinTable.TABLE_NAME}.${MealPlanRecipesJoinTable.COLUMN_MEAL_TYPE_ID} " +
                "WHERE ${MealTypesTable.TABLE_NAME}.${MealTypesTable.COLUMN_NAME} = :mealType " +
                "AND " +
                "${MealPlanTable.TABLE_NAME}.${MealPlanTable.COLUMN_DATE} = :selectedDate " +
                "AND ${RecipesTable.TABLE_NAME}.${RecipesTable.COLUMN_ID} = :recipeId)"
    )
    fun deleteRecipeFromMealPlanForDateRecipes(
        selectedDate: String,
        mealType: String,
        recipeId: Long
    )

}