package ksu.katara.healthymealplanner.mvvm.data.recipes.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "recipe_cuisine_types"
)
data class RecipeCuisineTypeDBEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "cuisine_type_id") val id: Long,
    @ColumnInfo(name = "cuisine_type_name") val name: String
)
