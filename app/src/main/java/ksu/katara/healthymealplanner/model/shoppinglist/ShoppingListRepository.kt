package ksu.katara.healthymealplanner.model.shoppinglist

import ksu.katara.healthymealplanner.model.recipes.entities.RecipeIngredient
import ksu.katara.healthymealplanner.tasks.Task

interface ShoppingListRepository {

    fun initShoppingList()

    fun addToShoppingList(ingredient: RecipeIngredient): Task<Unit>

    fun deleteFromShoppingList(ingredient: RecipeIngredient): Task<Unit>

    fun addShoppingListListener(listener: ShoppingListItemListListener)

    fun removeShoppingListListener(listener: ShoppingListItemListListener)

}
