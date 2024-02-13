package ksu.katara.healthymealplanner.mvvm.model.recipes.room.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "jt_recipe_ingredients_ingredient_measures",
    primaryKeys = ["rim_jt_recipe_ingredient_id", "rim_jt_ingredient_measure_id"],
    indices = [
        Index("rim_jt_ingredient_measure_id")
    ],
    foreignKeys = [
        ForeignKey(
            entity = RecipeIngredientMeasureDBEntity::class,
            parentColumns = ["ingredient_measure_id"],
            childColumns = ["rim_jt_ingredient_measure_id"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = RecipeIngredientDBEntity::class,
            parentColumns = ["ingredient_id"],
            childColumns = ["rim_jt_recipe_ingredient_id"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ]
)
data class RecipeIngredientsIngredientMeasuresJoinTableDBEntity(
    @ColumnInfo(name = "rim_jt_recipe_ingredient_id") val recipeIngredientId: Long,
    @ColumnInfo(name = "rim_jt_ingredient_measure_id") val ingredientMeasureId: Long
)