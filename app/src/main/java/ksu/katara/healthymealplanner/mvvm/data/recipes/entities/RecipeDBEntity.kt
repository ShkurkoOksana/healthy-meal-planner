package ksu.katara.healthymealplanner.mvvm.data.recipes.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import ksu.katara.healthymealplanner.mvvm.data.recipecategories.entities.RecipeCategoryDBEntity
import ksu.katara.healthymealplanner.mvvm.domain.recipes.entities.Recipe

@Entity(
    tableName = "recipes",
    indices = [
        Index("recipe_category_id"),
        Index("recipe_cuisine_type_id")
    ],
    foreignKeys = [
        ForeignKey(
            entity = RecipeCategoryDBEntity::class,
            parentColumns = ["category_id"],
            childColumns = ["recipe_category_id"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = RecipeCuisineTypeDBEntity::class,
            parentColumns = ["cuisine_type_id"],
            childColumns = ["recipe_cuisine_type_id"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ]
)
data class RecipeDBEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "recipe_id") val id: Long,
    @ColumnInfo(name = "recipe_category_id") val categoryId: Long,
    @ColumnInfo(name = "recipe_name") val name: String,
    @ColumnInfo(name = "recipe_photo") val photo: String,
    @ColumnInfo(name = "recipe_cuisine_type_id") val cuisineTypeId: Long,
    @ColumnInfo(name = "recipe_energetic_value") val energeticValue: Int,
    @ColumnInfo(name = "recipe_proteins_value") val proteins: Int,
    @ColumnInfo(name = "recipe_fats_value") val fats: Int,
    @ColumnInfo(name = "recipe_carbohydrates_value") val carbohydrates: Int,
    @ColumnInfo(name = "recipe_is_favorite") val isFavorite: Int,
    @ColumnInfo(name = "recipe_preparation_time") val preparationTime: Int,
) {

    fun toRecipe(): Recipe = Recipe(
        id = id,
        photo = photo,
        name = name,
        categoryId = categoryId
    )

}