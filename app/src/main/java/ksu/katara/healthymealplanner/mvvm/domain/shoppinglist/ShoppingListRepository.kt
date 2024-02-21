package ksu.katara.healthymealplanner.mvvm.domain.shoppinglist

import kotlinx.coroutines.flow.Flow
import ksu.katara.healthymealplanner.foundation.model.Repository
import ksu.katara.healthymealplanner.mvvm.domain.recipes.entities.RecipeIngredient
import ksu.katara.healthymealplanner.mvvm.domain.shoppinglist.entity.ShoppingListRecipe
import ksu.katara.healthymealplanner.mvvm.domain.shoppinglist.entity.ShoppingListRecipeIngredient

typealias ShoppingListListener = (shoppingList: MutableList<ShoppingListRecipe>) -> Unit

interface ShoppingListRepository : Repository {

    /**
     * Load the list of all available shopping list recipes that may be chosen by the user.
     */
    suspend fun loadShoppingList(): MutableList<ShoppingListRecipe>

    /**
     * Listen for for the current shopping list recipes changes.
     * @return [Flow] which emits a new item whenever [deleteIngredient] call
     * changes the current color.
     */
    fun shoppingListListener(): Flow<MutableList<ShoppingListRecipe>>

    /**
     * Set for ingredient property isInShoppingList equals isChecked for recipe in shopping list.
     */
    suspend fun selectIngredient(
        shoppingListRecipe: ShoppingListRecipe,
        shoppingListIngredient: ShoppingListRecipeIngredient,
        isChecked: Boolean
    )

    /**
     * Delete ingredient to shopping list for recipe with id.
     */
    fun deleteIngredient(recipeId: Long, ingredient: RecipeIngredient): Flow<Int>

}