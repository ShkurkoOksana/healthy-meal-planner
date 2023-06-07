package ksu.katara.healthymealplanner.model.shoppinglist

import ksu.katara.healthymealplanner.model.recipes.entities.RecipeIngredient
import ksu.katara.healthymealplanner.model.shoppinglist.entity.ShoppingListRecipe
import ksu.katara.healthymealplanner.model.shoppinglist.entity.ShoppingListRecipeIngredient
import ksu.katara.healthymealplanner.tasks.Task

interface ShoppingListRepository {

    fun loadShoppingList(): Task<Unit>

    fun addShoppingListListener(listener: ShoppingListListener)

    fun removeShoppingListListener(listener: ShoppingListListener)

    fun shoppingListIngredientsAddIngredient(recipeId: Long, ingredient: RecipeIngredient): Task<Unit>

    fun shoppingListIngredientsAddAllIngredients(recipeId: Long, isSelected: Boolean): Task<Unit>

    fun shoppingListIngredientsSelectIngredient(
        shoppingListRecipe: ShoppingListRecipe,
        shoppingListRecipeIngredient: ShoppingListRecipeIngredient,
        isChecked: Boolean
    ): Task<Unit>

    fun shoppingListIngredientsDeleteIngredient(recipeId: Long, ingredient: RecipeIngredient): Task<Unit>

}
