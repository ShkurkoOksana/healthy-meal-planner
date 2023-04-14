package ksu.katara.healthymealplanner.screens.main.tabs.home.diettips

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.healthymealplanner.R
import com.example.healthymealplanner.databinding.ItemDietTipDetailsBinding
import ksu.katara.healthymealplanner.model.dietTips.entities.DietTipDetails

class DietTipDetailsViewPagerAdapter(
    private val dietTipDetails: DietTipDetails,
) : RecyclerView.Adapter<DietTipDetailsViewPagerAdapter.DietTipDetailsViewPagerViewHolder>() {

    private val dietTipDetailsTitles = dietTipDetails.titlesList

    override fun getItemCount() = dietTipDetailsTitles.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DietTipDetailsViewPagerViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemDietTipDetailsBinding.inflate(inflater, parent, false)

        return DietTipDetailsViewPagerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DietTipDetailsViewPagerViewHolder, position: Int) {
        holder.binding.dietTipDetailsTitleTextView.text = dietTipDetails.titlesList[position]

        holder.binding.dietTipDetailsDescriptionTextView.text = dietTipDetails.descriptionsList[position]

        if (dietTipDetails.background[position].isNotBlank()) {
            Glide.with(holder.binding.dietTipDetailsBackgroundImageView.context)
                .load(dietTipDetails.background[position])
                .into(holder.binding.dietTipDetailsBackgroundImageView)
        } else {
            Glide.with(holder.binding.dietTipDetailsBackgroundImageView.context)
                .load(R.drawable.ic_diet_tip_details_default_background)
                .into(holder.binding.dietTipDetailsBackgroundImageView)
        }
    }

    class DietTipDetailsViewPagerViewHolder(
        val binding: ItemDietTipDetailsBinding
    ) : RecyclerView.ViewHolder(binding.root)
}