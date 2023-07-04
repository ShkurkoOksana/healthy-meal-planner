package ksu.katara.healthymealplanner.mvvm.views.main.tabs.home.recipedetails

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ksu.katara.healthymealplanner.databinding.ItemRecipeTypeBinding

class RecipeTypesDiffCallback(
    private val oldList: List<String>,
    private val newList: List<String>,
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldList.size
    override fun getNewListSize(): Int = newList.size
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldRecipeType = oldList[oldItemPosition]
        val newRecipeType = newList[newItemPosition]
        return oldRecipeType == newRecipeType
    }
    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldRecipeType = oldList[oldItemPosition]
        val newRecipeType = newList[newItemPosition]
        return oldRecipeType == newRecipeType
    }
}

class TypesAdapter : RecyclerView.Adapter<TypesAdapter.RecipeTypesHolder>() {

    var recipeTypes: List<String> = emptyList()
        set(newValue) {
            val diffCallback = RecipeTypesDiffCallback(field, newValue)
            val diffResult = DiffUtil.calculateDiff(diffCallback)
            field = newValue
            diffResult.dispatchUpdatesTo(this)
        }

    override fun getItemCount(): Int = recipeTypes.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeTypesHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemRecipeTypeBinding.inflate(inflater, parent, false)

        return RecipeTypesHolder(binding)
    }

    override fun onBindViewHolder(holder: RecipeTypesHolder, position: Int) {
        val recipeType = recipeTypes[position]
        holder.binding.recipeTypeButton.text = recipeType
    }

    class RecipeTypesHolder(
        val binding: ItemRecipeTypeBinding
    ) : RecyclerView.ViewHolder(binding.root)
}