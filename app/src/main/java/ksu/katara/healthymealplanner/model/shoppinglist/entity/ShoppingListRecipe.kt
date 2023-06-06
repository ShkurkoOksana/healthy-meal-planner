package ksu.katara.healthymealplanner.model.shoppinglist.entity

import ksu.katara.healthymealplanner.model.recipes.entities.Recipe
import ksu.katara.healthymealplanner.model.recipes.entities.RecipeIngredient

data class ShoppingListRecipe(
    val recipe: Recipe,
    var shoppingListIngredients: MutableList<ShoppingListRecipeIngredient>,
)

data class ShoppingListRecipeIngredient(
    val recipeIngredient: RecipeIngredient,
    var isSelectAndCross: Boolean
)