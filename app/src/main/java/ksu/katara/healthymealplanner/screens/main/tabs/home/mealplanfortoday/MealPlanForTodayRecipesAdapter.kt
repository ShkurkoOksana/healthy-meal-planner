package ksu.katara.healthymealplanner.screens.main.tabs.home.mealplanfortoday

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ksu.katara.healthymealplanner.R
import ksu.katara.healthymealplanner.databinding.ItemMealPlanForTodayRecipeBinding
import ksu.katara.healthymealplanner.model.recipes.entities.Recipe

interface MealPlanForTodayRecipeActionListener {

    fun onMealPlanForTodayRecipesItemDelete(recipe: Recipe)

    fun onMealPlanForTodayRecipesItemDetails(recipe: Recipe)

}

class MealPlanForTodayRecipesAdapter(
    private val mealPlanForTodayRecipeActionListener: MealPlanForTodayRecipeActionListener
) : RecyclerView.Adapter<MealPlanForTodayRecipesAdapter.MealPlanForTodayRecipesViewHolder>(), View.OnClickListener {

    var mealPlanForTodayRecipes: List<MealPlanForTodayRecipesItem> = emptyList()
        set(newValue) {
            field = newValue
            notifyDataSetChanged()
        }

    override fun onClick(v: View) {
        val recipe = v.tag as Recipe

        when (v.id) {
            R.id.mealPlanForTodayRecipesItemDeleteViewButton -> {
                mealPlanForTodayRecipeActionListener.onMealPlanForTodayRecipesItemDelete(recipe)
            }
            else -> {
                mealPlanForTodayRecipeActionListener.onMealPlanForTodayRecipesItemDetails(recipe)
            }
        }
    }

    override fun getItemCount(): Int = mealPlanForTodayRecipes.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MealPlanForTodayRecipesViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemMealPlanForTodayRecipeBinding.inflate(inflater, parent, false)

        binding.root.setOnClickListener(this)
        binding.mealPlanForTodayRecipesItemDeleteViewButton.setOnClickListener(this)

        return MealPlanForTodayRecipesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MealPlanForTodayRecipesViewHolder, position: Int) {
        val mealPlanForTodayRecipesItem = mealPlanForTodayRecipes[position]
        val recipe = mealPlanForTodayRecipesItem.recipe

        with(holder.binding) {
            holder.itemView.tag = recipe
            mealPlanForTodayRecipesItemDeleteViewButton.tag = recipe

            if (mealPlanForTodayRecipesItem.isInProgress) {
                mealPlanForTodayRecipesItemDeleteViewButton.visibility = View.INVISIBLE
                itemMealPlanForTodayRecipesItemProgressBar.visibility = View.VISIBLE
                holder.binding.root.setOnClickListener(null)
            } else {
                mealPlanForTodayRecipesItemDeleteViewButton.visibility = View.VISIBLE
                itemMealPlanForTodayRecipesItemProgressBar.visibility = View.GONE
                holder.binding.root.setOnClickListener(this@MealPlanForTodayRecipesAdapter)
            }

            mealPlanForTodayRecipesItemNameTextView.text = recipe.name
            if (recipe.photo.isNotBlank()) {
                Glide.with(mealPlanForTodayRecipesItemPhotoImageView.context)
                    .load(recipe.photo)
                    .circleCrop()
                    .placeholder(R.drawable.ic_recipe_default_photo)
                    .error(R.drawable.ic_recipe_default_photo)
                    .into(mealPlanForTodayRecipesItemPhotoImageView)
            } else {
                Glide.with(mealPlanForTodayRecipesItemPhotoImageView.context).clear(mealPlanForTodayRecipesItemPhotoImageView)
                mealPlanForTodayRecipesItemPhotoImageView.setImageResource(R.drawable.ic_diet_tip_default_photo)
            }
        }
    }

    class MealPlanForTodayRecipesViewHolder(
        val binding: ItemMealPlanForTodayRecipeBinding
    ) : RecyclerView.ViewHolder(binding.root)
}