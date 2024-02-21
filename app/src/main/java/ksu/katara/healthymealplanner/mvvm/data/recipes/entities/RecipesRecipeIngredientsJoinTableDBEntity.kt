package ksu.katara.healthymealplanner.mvvm.data.recipes.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "jt_recipes_ingredients",
    primaryKeys = ["ri_jt_recipe_id", "ri_jt_ingredient_id"],
    indices = [
        Index("ri_jt_ingredient_id")
    ],
    foreignKeys = [
        ForeignKey(
            entity = RecipeDBEntity::class,
            parentColumns = ["recipe_id"],
            childColumns = ["ri_jt_recipe_id"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = RecipeIngredientDBEntity::class,
            parentColumns = ["ingredient_id"],
            childColumns = ["ri_jt_ingredient_id"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ]
)
data class RecipesRecipeIngredientsJoinTableDBEntity(
    @ColumnInfo(name = "ri_jt_recipe_id") val recipeId: Long,
    @ColumnInfo(name = "ri_jt_ingredient_id") val ingredientId: Long,
    @ColumnInfo(name = "ri_jt_amount") val amount: String,
    @ColumnInfo(name = "ri_jt_is_in_shopping_list") val isInShoppingList: Int,
    @ColumnInfo(name = "ri_jt_is_cross_in_shopping_list") val isCrossInShoppingList: Int
)
