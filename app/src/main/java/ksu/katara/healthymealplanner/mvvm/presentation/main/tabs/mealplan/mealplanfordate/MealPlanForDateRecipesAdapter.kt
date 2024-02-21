package ksu.katara.healthymealplanner.mvvm.presentation.main.tabs.mealplan.mealplanfordate

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ksu.katara.healthymealplanner.R
import ksu.katara.healthymealplanner.databinding.ItemMealPlanForDateRecipeBinding
import ksu.katara.healthymealplanner.mvvm.domain.recipes.entities.Recipe

interface MealPlanDateRecipeActionListener {

    fun onMealPlanForDateRecipesItemDelete(recipe: Recipe)

    fun onMealPlanForDateRecipesItemDetails(recipe: Recipe)

}

class MealPlanForDateRecipesDiffCallback(
    private val oldList: List<MealPlanForDateRecipesItem>,
    private val newList: List<MealPlanForDateRecipesItem>,
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldList.size
    override fun getNewListSize(): Int = newList.size
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldMealPlanForDateRecipesItem = oldList[oldItemPosition]
        val newMealPlanForDateRecipesItem = newList[newItemPosition]
        return oldMealPlanForDateRecipesItem.recipe.id == newMealPlanForDateRecipesItem.recipe.id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldMealPlanForDateRecipesItem = oldList[oldItemPosition]
        val newMealPlanForDateRecipesItem = newList[newItemPosition]
        return oldMealPlanForDateRecipesItem.recipe == newMealPlanForDateRecipesItem.recipe && oldMealPlanForDateRecipesItem.isInProgress == newMealPlanForDateRecipesItem.isInProgress
    }
}

class MealPlanForDateRecipesAdapter(
    private val mealPlanDateRecipeActionListener: MealPlanDateRecipeActionListener
) : RecyclerView.Adapter<MealPlanForDateRecipesAdapter.MealPlanForDateRecipesViewHolder>(),
    View.OnClickListener {

    var mealPlanForDateRecipes: List<MealPlanForDateRecipesItem> = emptyList()
        set(newValue) {
            val diffCallback = MealPlanForDateRecipesDiffCallback(field, newValue)
            val diffResult = DiffUtil.calculateDiff(diffCallback)
            field = newValue
            diffResult.dispatchUpdatesTo(this)
        }

    override fun onClick(v: View) {
        val recipe = v.tag as Recipe

        when (v.id) {
            R.id.mealPlanForDateRecipeDeleteViewButton -> {
                mealPlanDateRecipeActionListener.onMealPlanForDateRecipesItemDelete(recipe)
            }

            else -> {
                mealPlanDateRecipeActionListener.onMealPlanForDateRecipesItemDetails(recipe)
            }
        }
    }

    override fun getItemCount(): Int = mealPlanForDateRecipes.size

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MealPlanForDateRecipesViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemMealPlanForDateRecipeBinding.inflate(inflater, parent, false)
        binding.root.setOnClickListener(this)
        binding.mealPlanForDateRecipeDeleteViewButton.setOnClickListener(this)
        return MealPlanForDateRecipesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MealPlanForDateRecipesViewHolder, position: Int) {
        val mealPlanForDateRecipesItem = mealPlanForDateRecipes[position]
        val recipe = mealPlanForDateRecipesItem.recipe
        with(holder.binding) {
            holder.itemView.tag = recipe
            mealPlanForDateRecipeDeleteViewButton.tag = recipe
            if (mealPlanForDateRecipesItem.isInProgress) {
                mealPlanForDateRecipeDeleteViewButton.visibility = View.INVISIBLE
                mealPlanForDateRecipeProgressBar.visibility = View.VISIBLE
                holder.binding.root.setOnClickListener(null)
            } else {
                mealPlanForDateRecipeDeleteViewButton.visibility = View.VISIBLE
                mealPlanForDateRecipeProgressBar.visibility = View.GONE
                holder.binding.root.setOnClickListener(this@MealPlanForDateRecipesAdapter)
            }
            mealPlanForDateRecipeNameTextView.text = recipe.name
            if (recipe.photo.isNotBlank()) {
                Glide.with(mealPlanForDateRecipePhotoImageView.context)
                    .load(recipe.photo)
                    .circleCrop()
                    .placeholder(R.drawable.ic_recipe)
                    .error(R.drawable.ic_recipe)
                    .into(mealPlanForDateRecipePhotoImageView)
            } else {
                Glide.with(mealPlanForDateRecipePhotoImageView.context)
                    .clear(mealPlanForDateRecipePhotoImageView)
                mealPlanForDateRecipePhotoImageView.setImageResource(R.drawable.ic_diet_tip)
            }
        }
    }

    class MealPlanForDateRecipesViewHolder(
        val binding: ItemMealPlanForDateRecipeBinding
    ) : RecyclerView.ViewHolder(binding.root)
}