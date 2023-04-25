package ksu.katara.healthymealplanner.model.shoppinglist

import ksu.katara.healthymealplanner.model.recipes.entities.RecipeIngredient
import ksu.katara.healthymealplanner.tasks.SimpleTask
import ksu.katara.healthymealplanner.tasks.Task
import java.util.concurrent.Callable

typealias ShoppingListItemListListener = (shoppingListItemList: MutableList<RecipeIngredient>) -> Unit

class InMemoryShoppingListRepository : ShoppingListRepository {

    private var shoppingListItemList: MutableList<RecipeIngredient> = mutableListOf()
    private var shoppingListLoaded = false

    private val shoppingListListeners = mutableSetOf<ShoppingListItemListListener>()

    override fun initShoppingList() {

    }

    override fun addToShoppingList(ingredient: RecipeIngredient): Task<Unit> =
        SimpleTask<Unit>(Callable {
            Thread.sleep(2000)

            shoppingListItemList.add(ingredient)

            notifyShoppingListChanges()
        })

    override fun deleteFromShoppingList(ingredient: RecipeIngredient): Task<Unit> =
        SimpleTask<Unit>(Callable {
            Thread.sleep(2000)

            val indexToDelete = shoppingListItemList.indexOfFirst { it.id == ingredient.id }
            if (indexToDelete != -1) {
                shoppingListItemList.removeAt(indexToDelete)

                notifyShoppingListChanges()
            }
        })

    override fun addShoppingListListener(listener: ShoppingListItemListListener) {
        shoppingListListeners.add(listener)
        if (shoppingListLoaded) {
            listener.invoke(shoppingListItemList)
        }
    }

    override fun removeShoppingListListener(listener: ShoppingListItemListListener) {
        shoppingListListeners.remove(listener)
    }

    private fun notifyShoppingListChanges() {
        if (!shoppingListLoaded) return
        shoppingListListeners.forEach { it.invoke(shoppingListItemList) }
    }
}
