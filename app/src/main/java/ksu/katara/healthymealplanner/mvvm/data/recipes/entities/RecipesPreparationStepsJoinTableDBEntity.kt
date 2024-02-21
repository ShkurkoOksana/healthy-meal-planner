package ksu.katara.healthymealplanner.mvvm.data.recipes.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "jt_recipes_preparation_steps",
    primaryKeys = ["ps_jt_recipe_id", "ps_jt_preparation_step_id"],
    indices = [
        Index("ps_jt_preparation_step_id")
    ],
    foreignKeys = [
        ForeignKey(
            entity = RecipeDBEntity::class,
            parentColumns = ["recipe_id"],
            childColumns = ["ps_jt_recipe_id"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = RecipePreparationStepDBEntity::class,
            parentColumns = ["recipe_preparation_step_id"],
            childColumns = ["ps_jt_preparation_step_id"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ]
)
data class RecipesPreparationStepsJoinTableDBEntity(
    @ColumnInfo(name = "ps_jt_recipe_id") val recipeId: Long,
    @ColumnInfo(name = "ps_jt_preparation_step_id") val preparationStepId: Long,
)
