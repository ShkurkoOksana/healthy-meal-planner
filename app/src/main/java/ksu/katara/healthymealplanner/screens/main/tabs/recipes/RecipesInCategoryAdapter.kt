package ksu.katara.healthymealplanner.screens.main.tabs.recipes

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ksu.katara.healthymealplanner.R
import ksu.katara.healthymealplanner.databinding.ItemRecipeInCategoryBinding
import ksu.katara.healthymealplanner.model.recipes.entities.Recipe

typealias RecipesInCategoryActionListener = (recipe: Recipe) -> Unit

class RecipesInCategoryAdapter(
    private val actionListener: RecipesInCategoryActionListener
) : RecyclerView.Adapter<RecipesInCategoryAdapter.RecipesInCategoryViewHolder>(), View.OnClickListener {

    var recipesInCategory: List<RecipesInCategoryListItem> = emptyList()
        set(newValue) {
            field = newValue
            notifyDataSetChanged()
        }

    override fun onClick(v: View) {
        val recipe = v.tag as Recipe
        actionListener.invoke(recipe)
    }

    override fun getItemCount(): Int = recipesInCategory.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipesInCategoryViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemRecipeInCategoryBinding.inflate(inflater, parent, false)

        return RecipesInCategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecipesInCategoryViewHolder, position: Int) {
        val recipeInCategoryListItem = recipesInCategory[position]
        val recipe = recipeInCategoryListItem.recipe

        with(holder.binding) {
            holder.itemView.tag = recipe

            if (recipeInCategoryListItem.isInProgress) {
                holder.binding.root.setOnClickListener(null)
            } else {
                holder.binding.root.setOnClickListener(this@RecipesInCategoryAdapter)
            }

            recipeInCategoryNameTextView.text = recipe.name
            if (recipe.photo.isNotBlank()) {
                Glide.with(recipeInCategoryPhotoImageView.context)
                    .load(recipe.photo)
                    .circleCrop()
                    .placeholder(R.drawable.ic_recipe_default_photo)
                    .error(R.drawable.ic_recipe_default_photo)
                    .into(recipeInCategoryPhotoImageView)
            } else {
                Glide.with(recipeInCategoryPhotoImageView.context).clear(recipeInCategoryPhotoImageView)
                recipeInCategoryPhotoImageView.setImageResource(R.drawable.ic_recipe_default_photo)
            }
        }
    }

    class RecipesInCategoryViewHolder(
        val binding: ItemRecipeInCategoryBinding
    ) : RecyclerView.ViewHolder(binding.root)
}