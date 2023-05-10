package ksu.katara.healthymealplanner.screens.main.tabs.home.mealplanfortoday.addrecipe

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ksu.katara.healthymealplanner.R
import ksu.katara.healthymealplanner.databinding.ItemAddRecipesPhotoBinding
import ksu.katara.healthymealplanner.model.addrecipephoto.entities.AddRecipePhoto
import ksu.katara.healthymealplanner.model.meal.enum.MealTypes

typealias OnAddRecipesPhotoListItemDelete = (mealType: MealTypes, addRecipePhoto: AddRecipePhoto) -> Unit

class AddRecipesPhotoListAdapter(
    private val mealType: MealTypes,
    private val addRecipesPhotoListViewModel: AddRecipesPhotoListViewModel
) : RecyclerView.Adapter<AddRecipesPhotoListAdapter.AddRecipesPhotoListViewHolder>(), View.OnClickListener {

    var addRecipesPhotoList: List<AddRecipesPhotoListItem> = emptyList()
        set(newValue) {
            field = newValue
            notifyDataSetChanged()
        }

    override fun onClick(view: View?) {
        val addRecipePhoto = view?.tag as AddRecipePhoto
        addRecipesPhotoListViewModel.invoke(mealType, addRecipePhoto)
    }

    override fun getItemCount(): Int = addRecipesPhotoList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddRecipesPhotoListViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemAddRecipesPhotoBinding.inflate(inflater, parent, false)

        binding.root.setOnClickListener(this)
        binding.addRecipesPhotoListImageView.setOnClickListener(this)

        return AddRecipesPhotoListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AddRecipesPhotoListViewHolder, position: Int) {
        val addRecipesPhotoItem = addRecipesPhotoList[position]
        val addRecipePhoto = addRecipesPhotoItem.addRecipePhoto

        with(holder.binding) {
            holder.itemView.tag = addRecipePhoto
            addRecipesPhotoListImageView.tag = addRecipePhoto

            if (addRecipesPhotoItem.isDeleteInProgress) {
                addRecipesPhotoListDeleteProgressBar.visibility = View.VISIBLE
                holder.binding.root.setOnClickListener(null)
            } else {
                addRecipesPhotoListDeleteProgressBar.visibility = View.GONE
                holder.binding.root.setOnClickListener(this@AddRecipesPhotoListAdapter)
            }

            if (addRecipePhoto.photo.isNotBlank()) {
                Glide.with(addRecipesPhotoListImageView.context)
                    .load(addRecipePhoto.photo)
                    .circleCrop()
                    .placeholder(R.drawable.ic_recipe_default_photo)
                    .error(R.drawable.ic_recipe_default_photo)
                    .into(addRecipesPhotoListImageView)
            } else {
                Glide.with(addRecipesPhotoListImageView.context).clear(addRecipesPhotoListImageView)
                addRecipesPhotoListImageView.setImageResource(R.drawable.ic_diet_tip_default_photo)
            }
        }
    }

    class AddRecipesPhotoListViewHolder(
        val binding: ItemAddRecipesPhotoBinding
    ) : RecyclerView.ViewHolder(binding.root)
}