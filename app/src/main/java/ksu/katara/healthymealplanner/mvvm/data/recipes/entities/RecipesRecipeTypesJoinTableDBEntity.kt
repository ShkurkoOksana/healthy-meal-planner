package ksu.katara.healthymealplanner.mvvm.data.recipes.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "jt_recipes_recipe_types",
    primaryKeys = ["rt_jt_recipe_id", "rt_jt_recipe_type_id"],
    indices = [
        Index("rt_jt_recipe_type_id")
    ],
    foreignKeys = [
        ForeignKey(
            entity = RecipeDBEntity::class,
            parentColumns = ["recipe_id"],
            childColumns = ["rt_jt_recipe_id"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = RecipeTypeDBEntity::class,
            parentColumns = ["type_id"],
            childColumns = ["rt_jt_recipe_type_id"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ]
)
data class RecipesRecipeTypesJoinTableDBEntity(
    @ColumnInfo(name = "rt_jt_recipe_id") val recipeId: Long,
    @ColumnInfo(name = "rt_jt_recipe_type_id") val recipeTypeId: Long,
)