package ksu.katara.healthymealplanner.model.recipes.entities

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import ksu.katara.healthymealplanner.model.product.entities.Product

@Parcelize
data class Recipe(
    val id: Long,
    val photo: String,
    val name: String,
) : Parcelable

data class RecipeDetails(
    val recipe: Recipe,
    val preparationTime: Int,
    val cuisineType: String,
    val types: List<String>,
    val energeticValue: Int,
    val proteins: Int,
    val fats: Int,
    val carbohydrates: Int,
    val recipeIngredients: MutableList<RecipeIngredient>,
    val preparationSteps: MutableList<RecipePreparationStep>,
    val isFavorite: Boolean,
    val isInShoppingList: Boolean,
)

data class RecipeIngredient(
    val id: Long,
    val product: Product,
    val amount: Double,
    val measure: String,
    var isInShoppingList: Boolean,
)

data class RecipePreparationStep(
    val id: Long,
    val step: Int,
    val photo: String,
    val description: String
)
