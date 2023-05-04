package ksu.katara.healthymealplanner.screens.main.tabs.home.mealplanfortoday.addrecipe

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ksu.katara.healthymealplanner.R
import ksu.katara.healthymealplanner.databinding.ItemAddRecipesRecipeBinding
import ksu.katara.healthymealplanner.model.meal.enum.MealTypes
import ksu.katara.healthymealplanner.model.recipes.entities.Recipe

typealias OnAddRecipesListItemDelete = (mealTypes: MealTypes, recipe: Recipe) -> Unit

class AddRecipesListAdapter(
    private val mealType: MealTypes,
    private val onAddRecipesListItemDelete: OnAddRecipesListItemDelete,
) : RecyclerView.Adapter<AddRecipesListAdapter.RecipesListViewHolder>(),
    View.OnClickListener,
    Filterable {

    var addRecipesList = mutableListOf<AddRecipesListItem>()
        set(newValue) {
            field = newValue
            notifyDataSetChanged()
        }

    var addRecipesListFilter = mutableListOf<AddRecipesListItem>()

    override fun onClick(v: View) {
        val recipe = v.tag as Recipe
        onAddRecipesListItemDelete.invoke(mealType, recipe)
    }

    override fun getItemCount(): Int = addRecipesList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipesListViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemAddRecipesRecipeBinding.inflate(inflater, parent, false)

        binding.root.setOnClickListener(this)

        return RecipesListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecipesListViewHolder, position: Int) {
        val recipesItem = addRecipesList[position]
        val recipe = recipesItem.recipe

        with(holder.binding) {
            holder.itemView.tag = recipe

            if (recipesItem.isDeleteInProgress) {
                addRecipesRecipeAddProgressBar.visibility = View.VISIBLE
                holder.binding.root.setOnClickListener(null)
            } else {
                addRecipesRecipeAddProgressBar.visibility = View.GONE
                holder.binding.root.setOnClickListener(this@AddRecipesListAdapter)
            }

            addRecipesRecipeNameTextView.text = recipe.name
            if (recipe.photo.isNotBlank()) {
                Glide.with(addRecipesRecipePhotoImageView.context)
                    .load(recipe.photo)
                    .circleCrop()
                    .placeholder(R.drawable.ic_recipe)
                    .error(R.drawable.ic_recipe)
                    .into(addRecipesRecipePhotoImageView)
            } else {
                Glide.with(addRecipesRecipePhotoImageView.context).clear(addRecipesRecipePhotoImageView)
                addRecipesRecipePhotoImageView.setImageResource(R.drawable.ic_diet_tip_default_photo)
            }
        }
    }

    class RecipesListViewHolder(
        val binding: ItemAddRecipesRecipeBinding
    ) : RecyclerView.ViewHolder(binding.root)

    override fun getFilter(): Filter {
        val filter: Filter = object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filterResults = FilterResults()
                if (constraint.isNullOrEmpty()) {
                    filterResults.values = addRecipesListFilter
                    filterResults.count = addRecipesListFilter.size
                } else {
                    val searchString = constraint.toString().lowercase()
                    val addRecipesFilterResult = mutableListOf<AddRecipesListItem>()
                    for (addRecipe in addRecipesListFilter) {
                        if (searchString in addRecipe.recipe.name.lowercase()) {
                            addRecipesFilterResult.add(addRecipe)
                        }
                    }
                    filterResults.values = addRecipesFilterResult
                    filterResults.count = addRecipesFilterResult.size
                }

                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults) {
                addRecipesList = results.values as MutableList<AddRecipesListItem>
                notifyDataSetChanged()
            }
        }

        return filter
    }
}