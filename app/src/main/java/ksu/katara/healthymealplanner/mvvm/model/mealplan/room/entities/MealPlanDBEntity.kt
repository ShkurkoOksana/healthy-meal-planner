package ksu.katara.healthymealplanner.mvvm.model.mealplan.room.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "meal_plan",
    indices = [
        Index(
            value = ["meal_plan_id", "meal_plan_date"],
            unique = true
        )
    ]
)
data class MealPlanDBEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "meal_plan_id") val id: Long,
    @ColumnInfo(name = "meal_plan_date") val date: String,
) {

    companion object {
        fun fromMealPlan(date: String): MealPlanDBEntity =
            MealPlanDBEntity(
                id = 0,
                date = date
            )
    }

}


