package ksu.katara.healthymealplanner.mvvm.views.main.tabs.recipes

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ksu.katara.healthymealplanner.R
import ksu.katara.healthymealplanner.databinding.ItemRecipeInCategoryBinding
import ksu.katara.healthymealplanner.mvvm.model.recipes.entities.Recipe

interface RecipesInCategoryActionListener {

    fun onRecipeInCategoryPressed(recipe: Recipe)

}

class RecipesInCategoryDiffCallback(
    private val oldList: List<Recipe>,
    private val newList: List<Recipe>,
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldList.size
    override fun getNewListSize(): Int = newList.size
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldRecipeInCategory = oldList[oldItemPosition]
        val newRecipeInCategory = newList[newItemPosition]
        return oldRecipeInCategory.id == newRecipeInCategory.id
    }
    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldRecipeInCategory = oldList[oldItemPosition]
        val newRecipeInCategory = newList[newItemPosition]
        return oldRecipeInCategory == newRecipeInCategory
    }
}

class RecipesInCategoryAdapter(
    private val actionListener: RecipesInCategoryActionListener
) : RecyclerView.Adapter<RecipesInCategoryAdapter.RecipesInCategoryViewHolder>(), View.OnClickListener {

    var recipesInCategory: List<Recipe> = emptyList()
        set(newValue) {
            val diffCallback = RecipesInCategoryDiffCallback(field, newValue)
            val diffResult = DiffUtil.calculateDiff(diffCallback)
            field = newValue
            diffResult.dispatchUpdatesTo(this)
        }

    override fun onClick(v: View) {
        val recipe = v.tag as Recipe
        actionListener.onRecipeInCategoryPressed(recipe)
    }

    override fun getItemCount(): Int = recipesInCategory.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipesInCategoryViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemRecipeInCategoryBinding.inflate(inflater, parent, false)
        return RecipesInCategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecipesInCategoryViewHolder, position: Int) {
        val recipe = recipesInCategory[position]

        with(holder.binding) {
            holder.itemView.tag = recipe
            holder.binding.root.setOnClickListener(this@RecipesInCategoryAdapter)
            recipeInCategoryNameTextView.text = recipe.name
            if (recipe.photo.isNotBlank()) {
                Glide.with(recipeInCategoryPhotoImageView.context)
                    .load(recipe.photo)
                    .circleCrop()
                    .placeholder(R.drawable.ic_recipe)
                    .error(R.drawable.ic_recipe)
                    .into(recipeInCategoryPhotoImageView)
            } else {
                Glide.with(recipeInCategoryPhotoImageView.context).clear(recipeInCategoryPhotoImageView)
                recipeInCategoryPhotoImageView.setImageResource(R.drawable.ic_recipe)
            }
        }
    }

    class RecipesInCategoryViewHolder(
        val binding: ItemRecipeInCategoryBinding
    ) : RecyclerView.ViewHolder(binding.root)
}