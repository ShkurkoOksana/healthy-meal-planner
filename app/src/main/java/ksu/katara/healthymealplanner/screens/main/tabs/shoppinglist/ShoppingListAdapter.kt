package ksu.katara.healthymealplanner.screens.main.tabs.shoppinglist

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ksu.katara.healthymealplanner.R
import ksu.katara.healthymealplanner.databinding.ItemShoppingListBinding
import ksu.katara.healthymealplanner.model.shoppinglist.entity.ShoppingListRecipe
import ksu.katara.healthymealplanner.model.shoppinglist.entity.ShoppingListRecipeIngredient

interface ShoppingListRecipeActionListener {

    fun onShoppingListRecipeDetails(shoppingListRecipe: ShoppingListRecipe)

    fun onShoppingListIngredientsRecipeDelete(shoppingListRecipe: ShoppingListRecipe, shoppingListRecipeIngredient: ShoppingListRecipeIngredient)

    fun onShoppingListIngredientsRecipeSelect(
        shoppingListRecipe: ShoppingListRecipe,
        shoppingListRecipeIngredient: ShoppingListRecipeIngredient,
        isChecked: Boolean
    )

}

class ShoppingListAdapter(
    private val context: Context,
    private val shoppingListViewModel: ShoppingListViewModel,
) : RecyclerView.Adapter<ShoppingListAdapter.ShoppingListViewHolder>(), View.OnClickListener {
    var shoppingList: List<ShoppingListRecipeItem> = emptyList()
        set(newValue) {
            field = newValue
            notifyDataSetChanged()
        }

    override fun onClick(v: View) {
        val shoppingListRecipe = v.tag as ShoppingListRecipe
        shoppingListViewModel.onShoppingListRecipeDetails(shoppingListRecipe)

    }

    override fun getItemCount() = shoppingList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShoppingListViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemShoppingListBinding.inflate(inflater, parent, false)

        return ShoppingListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ShoppingListViewHolder, position: Int) {
        val shoppingListRecipeItem = shoppingList[position]
        val shoppingListRecipe = shoppingListRecipeItem.recipe
        val shoppingListIngredientsItem = shoppingListRecipeItem.shoppingListIngredients
        val recipe = shoppingListRecipe.recipe

        with(holder.binding) {
            holder.itemView.tag = shoppingListRecipe

            holder.binding.root.setOnClickListener(this@ShoppingListAdapter)

            shoppingListRecipeNameTextView.text = recipe.name
            if (recipe.photo.isNotBlank()) {
                Glide.with(shoppingListRecipePhotoImageView.context)
                    .load(recipe.photo)
                    .circleCrop()
                    .placeholder(R.drawable.ic_recipe_default_photo)
                    .error(R.drawable.ic_recipe_default_photo)
                    .into(shoppingListRecipePhotoImageView)
            } else {
                Glide.with(shoppingListRecipePhotoImageView.context).clear(shoppingListRecipePhotoImageView)
                shoppingListRecipePhotoImageView.setImageResource(R.drawable.ic_recipe_default_photo)
            }
        }

        initShoppingListIngredients(shoppingListRecipe, shoppingListIngredientsItem, holder)
    }

    private fun initShoppingListIngredients(
        shoppingListRecipe: ShoppingListRecipe,
        shoppingListRecipeIngredients: MutableList<ShoppingListIngredientsItem>,
        holder: ShoppingListViewHolder
    ) {
        val shoppingListIngredientsAdapter = ShoppingListIngredientsAdapter(shoppingListRecipe, shoppingListViewModel)

        shoppingListIngredientsAdapter.shoppingListIngredients = shoppingListRecipeIngredients

        val shoppingListIngredientsLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        holder.binding.shoppingListIngredientsRecyclerView.layoutManager = shoppingListIngredientsLayoutManager
        holder.binding.shoppingListIngredientsRecyclerView.adapter = shoppingListIngredientsAdapter
    }

    class ShoppingListViewHolder(
        val binding: ItemShoppingListBinding
    ) : RecyclerView.ViewHolder(binding.root)
}