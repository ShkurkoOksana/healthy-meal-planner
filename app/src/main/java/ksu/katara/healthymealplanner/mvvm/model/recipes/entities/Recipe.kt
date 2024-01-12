package ksu.katara.healthymealplanner.mvvm.model.recipes.entities

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Represents recipe data
 */
@Parcelize
data class Recipe(
    val id: Long,
    val photo: String,
    val name: String,
    val categoryId: Long,
) : Parcelable

/**
 * Represents recipe details data
 */
data class RecipeDetails(
    val recipe: Recipe,
    val preparationTime: Int,
    val cuisineType: String,
    val types: List<String>,
    val energeticValue: Int,
    val proteins: Int,
    val fats: Int,
    val carbohydrates: Int,
    val ingredients: MutableList<RecipeIngredient>,
    val preparationSteps: MutableList<RecipePreparationStep>,
    val isFavorite: Boolean,
)

/**
 * Represents recipe ingredient data
 */
data class RecipeIngredient(
    val id: Long,
    val product: String,
    val amount: Double,
    val measure: String,
    var isInShoppingList: Boolean,
)

/**
 * Represents recipe preparation step data
 */
data class RecipePreparationStep(
    val id: Long,
    val step: Int,
    val photo: String,
    val description: String
)
