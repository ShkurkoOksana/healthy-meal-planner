package ksu.katara.healthymealplanner.mvvm.views.main.tabs.mealplan.addrecipes

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ksu.katara.healthymealplanner.R
import ksu.katara.healthymealplanner.databinding.ItemAddRecipesRecipeBinding
import ksu.katara.healthymealplanner.mvvm.model.recipes.entities.Recipe

interface AddRecipesActionListener {

    fun onAddRecipePressed(recipe: Recipe)

}

class AddRecipesListDiffCallback(
    private val oldList: List<AddRecipesItem>,
    private val newList: List<AddRecipesItem>,
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldList.size
    override fun getNewListSize(): Int = newList.size
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldAddRecipeItem = oldList[oldItemPosition]
        val newAddRecipeItem = newList[newItemPosition]
        return oldAddRecipeItem.recipe.id == newAddRecipeItem.recipe.id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldAddRecipeItem = oldList[oldItemPosition]
        val newAddRecipeItem = newList[newItemPosition]
        return oldAddRecipeItem.recipe == newAddRecipeItem.recipe && oldAddRecipeItem.isDeleteInProgress == newAddRecipeItem.isDeleteInProgress
    }
}

class AddRecipesAdapter(
    private val addRecipesActionListener: AddRecipesActionListener,
) : RecyclerView.Adapter<AddRecipesAdapter.RecipesListViewHolder>(),
    View.OnClickListener,
    Filterable {

    var addRecipesList = listOf<AddRecipesItem>()
        set(newValue) {
            val diffCallback = AddRecipesListDiffCallback(field, newValue)
            val diffResult = DiffUtil.calculateDiff(diffCallback)
            field = newValue
            diffResult.dispatchUpdatesTo(this)
        }
    var addRecipesListFilter = listOf<AddRecipesItem>()

    override fun onClick(v: View) {
        val recipe = v.tag as Recipe
        addRecipesActionListener.onAddRecipePressed(recipe)
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
                addRecipeAddProgressBar.visibility = View.VISIBLE
                holder.binding.root.setOnClickListener(null)
            } else {
                addRecipeAddProgressBar.visibility = View.INVISIBLE
                holder.binding.root.setOnClickListener(this@AddRecipesAdapter)
            }
            addRecipeNameTextView.text = recipe.name
            if (recipe.photo.isNotBlank()) {
                Glide.with(addRecipePhotoImageView.context)
                    .load(recipe.photo)
                    .circleCrop()
                    .placeholder(R.drawable.ic_recipe)
                    .error(R.drawable.ic_recipe)
                    .into(addRecipePhotoImageView)
            } else {
                Glide.with(addRecipePhotoImageView.context).clear(addRecipePhotoImageView)
                addRecipePhotoImageView.setImageResource(R.drawable.ic_diet_tip)
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
                    val addRecipesFilterResult = mutableListOf<AddRecipesItem>()
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
                addRecipesList = results.values as List<AddRecipesItem>
                notifyDataSetChanged()
            }
        }

        return filter
    }
}