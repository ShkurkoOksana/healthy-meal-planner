package ksu.katara.healthymealplanner.mvvm.data.mealplan.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "meal_types"
)
data class MealTypeDBEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "meal_type_id") val id: Long,
    @ColumnInfo(name = "meal_type_name") val name: String
)