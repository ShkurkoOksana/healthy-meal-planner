package ksu.katara.healthymealplanner.mvvm.data.recipes.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "recipe_types"
)
data class RecipeTypeDBEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "type_id") val id: Long,
    @ColumnInfo(name = "type_name") val name: String
)