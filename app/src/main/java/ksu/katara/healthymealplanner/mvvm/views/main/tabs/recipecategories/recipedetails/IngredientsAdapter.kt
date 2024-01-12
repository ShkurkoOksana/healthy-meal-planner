package ksu.katara.healthymealplanner.mvvm.views.main.tabs.recipecategories.recipedetails

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ksu.katara.healthymealplanner.databinding.ItemIngredientsBinding
import ksu.katara.healthymealplanner.mvvm.model.recipes.entities.RecipeIngredient

interface IngredientSelectedActionListener {

    fun onIngredientPressed(ingredient: RecipeIngredient, isSelected: Boolean)

}

class IngredientsDiffCallback(
    private val oldList: List<IngredientsItem>,
    private val newList: List<IngredientsItem>,
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldList.size
    override fun getNewListSize(): Int = newList.size
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldIngredientsItem = oldList[oldItemPosition]
        val newIngredientsItem = newList[newItemPosition]
        return oldIngredientsItem.ingredient.id == newIngredientsItem.ingredient.id
    }
    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldIngredientsItem = oldList[oldItemPosition]
        val newIngredientsItem = newList[newItemPosition]
        return oldIngredientsItem.ingredient == newIngredientsItem.ingredient && oldIngredientsItem.isInProgress == newIngredientsItem.isInProgress
    }
}

class IngredientsAdapter(
    private val recipeDetailsViewModel: RecipeDetailsViewModel
) : RecyclerView.Adapter<IngredientsAdapter.IngredientsListViewHolder>(), View.OnClickListener {

    var ingredients: List<IngredientsItem> = emptyList()
        set(newValue) {
            val diffCallback = IngredientsDiffCallback(field, newValue)
            val diffResult = DiffUtil.calculateDiff(diffCallback)
            field = newValue
            diffResult.dispatchUpdatesTo(this)
        }

    override fun onClick(v: View) {
        val ingredient = v.tag as RecipeIngredient
        val isSelected = !ingredient.isInShoppingList
        recipeDetailsViewModel.onIngredientPressed(ingredient, isSelected)
    }

    override fun getItemCount(): Int = ingredients.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IngredientsListViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemIngredientsBinding.inflate(inflater, parent, false)
        binding.root.setOnClickListener(this)
        return IngredientsListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: IngredientsListViewHolder, position: Int) {
        val ingredientsItemListItem = ingredients[position]
        val ingredient = ingredientsItemListItem.ingredient
        with(holder.binding) {
            holder.itemView.tag = ingredient

            if (ingredientsItemListItem.isInProgress) {
                isIngredientSelectedCheckBox.visibility = View.INVISIBLE
                ingredientCrossView.visibility = View.INVISIBLE
                isIngredientSelectedProgressBar.visibility = View.VISIBLE
                holder.binding.root.setOnClickListener(null)
            } else {
                if (ingredient.isInShoppingList) {
                    ingredientCrossView.visibility = View.VISIBLE
                    isIngredientSelectedCheckBox.isChecked = true
                } else {
                    ingredientCrossView.visibility = View.INVISIBLE
                    isIngredientSelectedCheckBox.isChecked = false
                }

                isIngredientSelectedCheckBox.visibility = View.VISIBLE
                isIngredientSelectedProgressBar.visibility = View.GONE
                holder.binding.root.setOnClickListener(this@IngredientsAdapter)
            }
            ingredientNameTextView.text = ingredient.product
            if (ingredient.amount == 0.0) {
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