package ksu.katara.healthymealplanner.screens.main.tabs.home.recipe.ingredients

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ksu.katara.healthymealplanner.databinding.ItemIngredientsBinding
import ksu.katara.healthymealplanner.model.recipes.entities.RecipeIngredient

typealias IngredientsAddDeleteToFromShoppingListActionListener = (ingredient: RecipeIngredient) -> Unit

class IngredientsAdapter(
    private val actionListener: IngredientsAddDeleteToFromShoppingListActionListener
) : RecyclerView.Adapter<IngredientsAdapter.IngredientsListViewHolder>(), View.OnClickListener {

    var ingredients: List<IngredientsItem> = emptyList()
        set(newValue) {
            field = newValue
            notifyDataSetChanged()
        }

    override fun onClick(v: View) {
        val ingredient = v.tag as RecipeIngredient
        actionListener.invoke(ingredient)
        ingredient.isInShoppingList = !ingredient.isInShoppingList
    }

    override fun getItemCount(): Int = ingredients.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IngredientsListViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemIngredientsBinding.inflate(inflater, parent, false)

        return IngredientsListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: IngredientsListViewHolder, position: Int) {
        val ingredientsItemListItem = ingredients[position]
        val ingredient = ingredientsItemListItem.ingredient

        with(holder.binding) {
            holder.itemView.tag = ingredient

            if (ingredientsItemListItem.isInProgress) {
                ingredientCheckBox.visibility = View.INVISIBLE
                ingredientCrossView.visibility = View.INVISIBLE
                ingredientIsInShoppingListProgressBar.visibility = View.VISIBLE
                holder.binding.root.setOnClickListener(null)
            } else {
                if(ingredient.isInShoppingList) {
                    ingredientCrossView.visibility = View.VISIBLE
                    ingredientCheckBox.isChecked = true
                } else {
                    ingredientCrossView.visibility = View.INVISIBLE
                    ingredientCheckBox.isChecked = false
                }

                ingredientCheckBox.visibility = View.VISIBLE
                ingredientIsInShoppingListProgressBar.visibility = View.GONE
                holder.binding.root.setOnClickListener(this@IngredientsAdapter)
            }

            ingredientNameTextView.text = ingredient.product.name

            if(ingredient.amount == 0.0) {
                ingredientAmountTextView.text = ""
            } else {
                ingredientAmountTextView.text = ingredient.amount.toString()
            }

            ingredientMeasureTextView.text = ingredient.measure
        }
    }

    class IngredientsListViewHolder(
        val binding: ItemIngredientsBinding
    ) : RecyclerView.ViewHolder(binding.root)
}