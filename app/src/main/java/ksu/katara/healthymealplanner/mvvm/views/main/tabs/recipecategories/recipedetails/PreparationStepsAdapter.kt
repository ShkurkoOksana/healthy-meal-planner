package ksu.katara.healthymealplanner.mvvm.views.main.tabs.recipecategories.recipedetails

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ksu.katara.healthymealplanner.R
import ksu.katara.healthymealplanner.databinding.ItemPreparationStepsBinding
import ksu.katara.healthymealplanner.mvvm.model.recipes.entities.RecipePreparationStep

class RecipePreparationStepsDiffCallback(
    private val oldList: List<RecipePreparationStep>,
    private val newList: List<RecipePreparationStep>,
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldList.size
    override fun getNewListSize(): Int = newList.size
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldRecipePreparationStep = oldList[oldItemPosition]
        val newRecipePreparationStep = newList[newItemPosition]
        return oldRecipePreparationStep.id == newRecipePreparationStep.id
    }
    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldRecipePreparationStep = oldList[oldItemPosition]
        val newRecipePreparationStep = newList[newItemPosition]
        return oldRecipePreparationStep == newRecipePreparationStep
    }
}

class PreparationStepsAdapter : RecyclerView.Adapter<PreparationStepsAdapter.PreparationStepsHolder>() {

    var preparationSteps: List<RecipePreparationStep> = emptyList()
        set(newValue) {
            val diffCallback = RecipePreparationStepsDiffCallback(field, newValue)
            val diffResult = DiffUtil.calculateDiff(diffCallback)
            field = newValue
            diffResult.dispatchUpdatesTo(this)
        }

    override fun getItemCount(): Int = preparationSteps.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PreparationStepsHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemPreparationStepsBinding.inflate(inflater, parent, false)
        return PreparationStepsHolder(binding)
    }

    override fun onBindViewHolder(holder: PreparationStepsHolder, position: Int) {
        val preparationStep = preparationSteps[position]
        with(holder.binding) {
            if (preparationStep.photo.isNotBlank()) {
                Glide.with(preparationStepPhotoImageView.context)
                    .load(preparationStep.photo)
                    .circleCrop()
                    .placeholder(R.drawable.ic_recipe)
                    .error(R.drawable.ic_recipe)
                    .into(preparationStepPhotoImageView)
            } else {
                Glide.with(preparationStepPhotoImageView.context).clear(preparationStepPhotoImageView)
                preparationStepPhotoImageView.setImageResource(R.drawable.ic_recipe)
            }
            preparationStepTitleTextView.text = preparationStep.step.toString()
            preparationStepDescriptionTextView.text = preparationStep.description
        }
    }

    class PreparationStepsHolder(
        val binding: ItemPreparationStepsBinding
    ) : RecyclerView.ViewHolder(binding.root)
}