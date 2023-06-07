package ksu.katara.healthymealplanner.screens.main.tabs.mealplan.mealplanfordate

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ksu.katara.healthymealplanner.R
import ksu.katara.healthymealplanner.databinding.ItemMealPlanForDateRecipeBinding
import ksu.katara.healthymealplanner.model.recipes.entities.Recipe

interface MealPlanDateRecipeActionListener {

    fun onMealPlanForDateRecipesItemDelete(recipe: Recipe)

    fun onMealPlanForDateRecipesItemDetails(recipe: Recipe)

}

class MealPlanForDateRecipesAdapter(
    private val mealPlanDateRecipeActionListener: MealPlanDateRecipeActionListener
) : RecyclerView.Adapter<MealPlanForDateRecipesAdapter.MealPlanForDateRecipesViewHolder>(), View.OnClickListener {

    var mealPlanForDateRecipes: List<MealPlanForDateRecipesItem> = emptyList()
        set(newValue) {
            field = newValue
            notifyDataSetChanged()
        }

    override fun onClick(v: View) {
        val recipe = v.tag as Recipe

        when (v.id) {
            R.id.mealPlanForDateRecipesItemDeleteViewButton -> {
                mealPlanDateRecipeActionListener.onMealPlanForDateRecipesItemDelete(recipe)
            }

            else -> {
                mealPlanDateRecipeActionListener.onMealPlanForDateRecipesItemDetails(recipe)
            }
        }
    }

    override fun getItemCount(): Int = mealPlanForDateRecipes.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MealPlanForDateRecipesViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemMealPlanForDateRecipeBinding.inflate(inflater, parent, false)

        binding.root.setOnClickListener(this)
        binding.mealPlanForDateRecipesItemDeleteViewButton.setOnClickListener(this)

        return MealPlanForDateRecipesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MealPlanForDateRecipesViewHolder, position: Int) {
        val mealPlanForDateRecipesItem = mealPlanForDateRecipes[position]
        val recipe = mealPlanForDateRecipesItem.recipe

        with(holder.binding) {
            holder.itemView.tag = recipe
            mealPlanForDateRecipesItemDeleteViewButton.tag = recipe

            if (mealPlanForDateRecipesItem.isInProgress) {
                mealPlanForDateRecipesItemDeleteViewButton.visibility = View.INVISIBLE
                itemMealPlanForDateRecipesItemProgressBar.visibility = View.VISIBLE
                holder.binding.root.setOnClickListener(null)
            } else {
                mealPlanForDateRecipesItemDeleteViewButton.visibility = View.VISIBLE
                itemMealPlanForDateRecipesItemProgressBar.visibility = View.GONE
                holder.binding.root.setOnClickListener(this@MealPlanForDateRecipesAdapter)
            }

            mealPlanForDateRecipesItemNameTextView.text = recipe.name
            if (recipe.photo.isNotBlank()) {
                Glide.with(mealPlanForDateRecipesItemPhotoImageView.context)
                    .load(recipe.photo)
                    .circleCrop()
                    .placeholder(R.drawable.ic_recipe_default_photo)
                    .error(R.drawable.ic_recipe_default_photo)
                    .into(mealPlanForDateRecipesItemPhotoImageView)
            } else {
                Glide.with(mealPlanForDateRecipesItemPhotoImageView.context).clear(mealPlanForDateRecipesItemPhotoImageView)
                mealPlanForDateRecipesItemPhotoImageView.setImageResource(R.drawable.ic_diet_tip_default_photo)
            }
        }
    }

    class MealPlanForDateRecipesViewHolder(
        val binding: ItemMealPlanForDateRecipeBinding
    ) : RecyclerView.ViewHolder(binding.root)
}