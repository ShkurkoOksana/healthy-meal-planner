package ksu.katara.healthymealplanner.mvvm.model.recipes.room.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "ingredient_measures"
)
data class RecipeIngredientMeasureDBEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "ingredient_measure_id") val id: Long,
    @ColumnInfo(name = "ingredient_measure_name") val name: String
)


