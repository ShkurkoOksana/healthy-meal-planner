package ksu.katara.healthymealplanner.mvvm.data.recipecategories.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import ksu.katara.healthymealplanner.mvvm.domain.recipecategories.entities.RecipeCategory

@Entity(
    tableName = "recipe_categories"
)
data class RecipeCategoryDBEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "category_id") val id: Long,
    @ColumnInfo(name = "category_photo") val photo: String,
    @ColumnInfo(name = "category_name") val name: String
) {

    fun toRecipeCategory(): RecipeCategory = RecipeCategory(
        id = id,
        photo = photo,
        name = name
    )

}