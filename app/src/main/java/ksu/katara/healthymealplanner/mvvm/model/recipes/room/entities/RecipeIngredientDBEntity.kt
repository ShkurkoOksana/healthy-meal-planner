package ksu.katara.healthymealplanner.mvvm.model.recipes.room.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "recipe_ingredients"
)
data class RecipeIngredientDBEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "ingredient_id") val id: Long,
    @ColumnInfo(name = "ingredient_name") val name: String
)