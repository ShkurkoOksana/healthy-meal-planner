package ksu.katara.healthymealplanner.mvvm.views.main.tabs.home.diettips

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ksu.katara.healthymealplanner.databinding.ItemDietTipDetailsBinding
import ksu.katara.healthymealplanner.mvvm.model.dietTips.entities.DietTipDetailSteps

class DietTipDetailsViewPagerAdapter(
    private val dietTipsViewModel: DietTipDetailsViewModel,
    private val dietTipDetailSteps: List<DietTipDetailSteps>,
) : RecyclerView.Adapter<DietTipDetailsViewPagerAdapter.DietTipDetailsViewPagerViewHolder>() {

    override fun getItemCount() = dietTipDetailSteps.size

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DietTipDetailsViewPagerViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemDietTipDetailsBinding.inflate(inflater, parent, false)

        return DietTipDetailsViewPagerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DietTipDetailsViewPagerViewHolder, position: Int) {
        val dietTipDetailsId = dietTipDetailSteps[position].dietTipDetailId
        val background = dietTipsViewModel.getDietTipDetailsById(dietTipDetailsId).background
        holder.binding.dietTipDetailsTitleTextView.text = dietTipDetailSteps[position].title
        holder.binding.dietTipDetailsDescriptionTextView.text =
            dietTipDetailSteps[position].description
        Glide.with(holder.binding.dietTipDetailsBackgroundImageView.context)
            .load(background)
            .into(holder.binding.dietTipDetailsBackgroundImageView)
    }

    class DietTipDetailsViewPagerViewHolder(
        val binding: ItemDietTipDetailsBinding
    ) : RecyclerView.ViewHolder(binding.root)
}