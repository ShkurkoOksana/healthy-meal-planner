package ksu.katara.healthymealplanner.mvvm.domain.shoppinglist.entity

import ksu.katara.healthymealplanner.mvvm.domain.recipes.entities.Recipe
import ksu.katara.healthymealplanner.mvvm.domain.recipes.entities.RecipeIngredient

/**
 * Represents shopping list recipe data
 */
data class ShoppingListRecipe(
    val recipe: Recipe,
    var ingredients: MutableList<ShoppingListRecipeIngredient>,
)

/**
 * Represents shopping list recipe ingredient data
 */
data class ShoppingListRecipeIngredient(
    val ingredient: RecipeIngredient,
    var isSelectAndCross: Boolean
)