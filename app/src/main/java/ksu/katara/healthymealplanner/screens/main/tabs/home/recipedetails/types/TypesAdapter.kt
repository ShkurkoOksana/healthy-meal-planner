package ksu.katara.healthymealplanner.screens.main.tabs.home.recipedetails.types

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ksu.katara.healthymealplanner.databinding.ItemRecipeTypeBinding

class TypesAdapter : RecyclerView.Adapter<TypesAdapter.RecipeTypesHolder>() {

    var recipeTypes: List<String> = emptyList()
        set(newValue) {
            field = newValue
            notifyDataSetChanged()
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