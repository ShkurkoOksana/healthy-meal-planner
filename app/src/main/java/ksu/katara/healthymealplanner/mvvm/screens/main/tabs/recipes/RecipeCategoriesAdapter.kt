package ksu.katara.healthymealplanner.mvvm.screens.main.tabs.recipes

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ksu.katara.healthymealplanner.R
import ksu.katara.healthymealplanner.databinding.ItemRecipeInCategoryBinding
import ksu.katara.healthymealplanner.mvvm.model.recipecategories.entities.Category

typealias RecipeCategoryActionListener = (recipeCategory: Category) -> Unit

class RecipeCategoriesDiffCallback(
    private val oldList: List<Category>,
    private val newList: List<Category>,
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldList.size
    override fun getNewListSize(): Int = newList.size
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldRecipeCategory = oldList[oldItemPosition]
        val newRecipeCategory = newList[newItemPosition]
        return oldRecipeCategory.id == newRecipeCategory.id
    }
    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldRecipeCategory = oldList[oldItemPosition]
        val newRecipeCategory = newList[newItemPosition]
        return oldRecipeCategory == newRecipeCategory
    }
}

class RecipeCategoriesAdapter(
    private val actionListener: RecipeCategoryActionListener
) : RecyclerView.Adapter<RecipeCategoriesAdapter.RecipeCategoriesViewHolder>(), View.OnClickListener {

    var recipeCategories: List<Category> = emptyList()
        set(newValue) {
            val diffCallback = RecipeCategoriesDiffCallback(field, newValue)
            val diffResult = DiffUtil.calculateDiff(diffCallback)
            field = newValue
            diffResult.dispatchUpdatesTo(this)
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
        val recipeCategory = recipeCategories[position]

        with(holder.binding) {
            holder.itemView.tag = recipeCategory
            holder.binding.root.setOnClickListener(this@RecipeCategoriesAdapter)
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