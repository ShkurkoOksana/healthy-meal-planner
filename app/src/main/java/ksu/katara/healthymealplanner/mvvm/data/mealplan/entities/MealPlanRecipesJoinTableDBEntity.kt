package ksu.katara.healthymealplanner.mvvm.data.mealplan.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import ksu.katara.healthymealplanner.mvvm.data.recipes.entities.RecipeDBEntity

@Entity(
    tableName = "jt_meal_plan_recipes",
    primaryKeys = ["mp_jt_meal_plan_id", "mp_jt_recipe_id", "mp_jt_meal_type_id"],
    indices = [
        Index(value = ["mp_jt_meal_type_id", "mp_jt_recipe_id"]),
        Index("mp_jt_recipe_id")
    ],
    foreignKeys = [
        ForeignKey(
            entity = MealPlanDBEntity::class,
            parentColumns = ["meal_plan_id"],
            childColumns = ["mp_jt_meal_plan_id"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = RecipeDBEntity::class,
            parentColumns = ["recipe_id"],
            childColumns = ["mp_jt_recipe_id"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = MealTypeDBEntity::class,
            parentColumns = ["meal_type_id"],
            childColumns = ["mp_jt_meal_type_id"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ]
)
data class MealPlanRecipesJoinTableDBEntity(
    @ColumnInfo(name = "mp_jt_meal_plan_id") val mealPlanId: Long,
    @ColumnInfo(name = "mp_jt_recipe_id") val recipeId: Long,
    @ColumnInfo(name = "mp_jt_meal_type_id") val mealTypeId: Long
) {

    companion object {

        fun fromMealPlanAndRecipe(
            mealPlanId: Long,
            recipeId: Long,
            mealTypeId: Long
        ): MealPlanRecipesJoinTableDBEntity = MealPlanRecipesJoinTableDBEntity(
            mealPlanId = mealPlanId,
            recipeId = recipeId,
            mealTypeId = mealTypeId
        )

    }

}
