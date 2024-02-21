package ksu.katara.healthymealplanner.mvvm.data.recipes.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import ksu.katara.healthymealplanner.mvvm.domain.recipes.entities.RecipePreparationStep

@Entity(
    tableName = "recipe_preparation_steps"
)
data class RecipePreparationStepDBEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo("recipe_preparation_step_id") val id: Long,
    @ColumnInfo(name = "recipe_preparation_step_photo") val photo: String,
    @ColumnInfo(name = "recipe_preparation_step_description") val description: String,
    @ColumnInfo(name = "recipe_preparation_step") val preparationStep: Int,
) {

    fun toRecipePreparationStep(): RecipePreparationStep = RecipePreparationStep(
        id = id,
        step = preparationStep,
        photo = photo,
        description = description
    )

}

