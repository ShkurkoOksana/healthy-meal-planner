package ksu.katara.healthymealplanner.screens.main.tabs.recipes

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ksu.katara.healthymealplanner.R
import ksu.katara.healthymealplanner.databinding.ItemRecipeInCategoryBinding
import ksu.katara.healthymealplanner.model.recipecategories.entities.Category

typealias RecipeCategoryActionListener = (recipeCategory: Category) -> Unit

class RecipeCategoriesAdapter(
    private val actionListener: RecipeCategoryActionListener
) : RecyclerView.Adapter<RecipeCategoriesAdapter.RecipeCategoriesViewHolder>(), View.OnClickListener {

    var recipeCategories: List<RecipeCategoriesListItem> = emptyList()
        set(newValue) {
            field = newValue
            notifyDataSetChanged()
        }

    override fun onClick(v: View) {
        val recipeCategory = v.tag as Category
        actionListener.invoke(recipeCategory)
    }

    override fun getItemCount(): Int = recipeCategories.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeCategoriesViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemRecipeInCategoryBinding.inflate(inflater, parent, false)

        return RecipeCategoriesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecipeCategoriesViewHolder, position: Int) {
        val recipeCategoryListItem = recipeCategories[position]
        val recipeCategory = recipeCategoryListItem.recipeCategory

        with(holder.binding) {
            holder.itemView.tag = recipeCategory

            if (recipeCategoryListItem.isInProgress) {
                holder.binding.root.setOnClickListener(null)
            } else {
                holder.binding.root.setOnClickListener(this@RecipeCategoriesAdapter)
            }

            recipeInCategoryNameTextView.text = recipeCategory.name
            if (recipeCategory.photo.isNotBlank()) {
                Glide.with(recipeInCategoryPhotoImageView.context)
                    .load(recipeCategory.photo)
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

    class RecipeCategoriesViewHolder(
        val binding: ItemRecipeInCategoryBinding
    ) : RecyclerView.ViewHolder(binding.root)
}