package ksu.katara.healthymealplanner.mvvm.presentation.main.tabs.home.diettips

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ksu.katara.healthymealplanner.databinding.ItemDietTipDetailsBinding

class DietTipDetailsViewPagerAdapter(
    private val dietTipDetailSteps: DietTipDetailsSteps,
) : RecyclerView.Adapter<DietTipDetailsViewPagerAdapter.DietTipDetailsViewPagerViewHolder>() {

    override fun getItemCount() = dietTipDetailSteps.steps.size

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DietTipDetailsViewPagerViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemDietTipDetailsBinding.inflate(inflater, parent, false)

        return DietTipDetailsViewPagerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DietTipDetailsViewPagerViewHolder, position: Int) {
        val background = dietTipDetailSteps.dietTipDetails.background
        holder.binding.dietTipDetailsTitleTextView.text = dietTipDetailSteps.steps[position].title
        holder.binding.dietTipDetailsDescriptionTextView.text =
            dietTipDetailSteps.steps[position].description
        Glide.with(holder.binding.dietTipDetailsBackgroundImageView.context)
            .load(background)
            .into(holder.binding.dietTipDetailsBackgroundImageView)
    }

    class DietTipDetailsViewPagerViewHolder(
        val binding: ItemDietTipDetailsBinding
    ) : RecyclerView.ViewHolder(binding.root)
}