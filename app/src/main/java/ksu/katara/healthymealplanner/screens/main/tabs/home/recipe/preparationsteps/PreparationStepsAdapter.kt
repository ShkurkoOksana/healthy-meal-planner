package ksu.katara.healthymealplanner.screens.main.tabs.home.recipe.preparationsteps

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ksu.katara.healthymealplanner.R
import ksu.katara.healthymealplanner.databinding.ItemPreparationStepsBinding
import ksu.katara.healthymealplanner.model.recipes.entities.RecipePreparationStep

class PreparationStepsAdapter : RecyclerView.Adapter<PreparationStepsAdapter.PreparationStepsHolder>() {

    var preparationSteps: List<RecipePreparationStep> = emptyList()
        set(newValue) {
            field = newValue
            notifyDataSetChanged()
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
                    .placeholder(R.drawable.ic_recipe_default_photo)
                    .error(R.drawable.ic_recipe_default_photo)
                    .into(preparationStepPhotoImageView)
            } else {
                Glide.with(preparationStepPhotoImageView.context).clear(preparationStepPhotoImageView)
                preparationStepPhotoImageView.setImageResource(R.drawable.ic_recipe_default_photo)
            }

            preparationStepTitleTextView.text = preparationStep.step.toString()
            preparationStepDescriptionTextView.text = preparationStep.description
        }
    }

    class PreparationStepsHolder(
        val binding: ItemPreparationStepsBinding
    ) : RecyclerView.ViewHolder(binding.root)
}